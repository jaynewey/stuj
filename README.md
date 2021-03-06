# stuj

![GitHub](https://img.shields.io/github/license/jaynewey/stuj)


> A tiny, lightweight Entity Component System Framework for Java.

Stuj is an Entity Component System framework written in Java. It aims to provide a similar interface to [stup](https://github.com/jaynewey/stup-ecs), an Entity Component System Framework written in Python.

# Contents

* [Entity](#entity)
* [EntityManager](#entitymanager)
    + [deltatime](#deltatime)
* [Component](#component)
    + [Adding and Removing Components](#adding-and-removing-components)
* [EntitySystem](#entitysystem)
    + [System Priority](#system-priority)
    + [IteratorSystem](#iteratorsystem)
* [EntityListener](#entitylistener)

# Usage

## Entity

Entities are are Universally Unique Identifiers (UUIDs) and nothing else. Entities can be instantiated directly and then added to the manager:

```java
EntityManager entityManager = new EntityManager();
Entity entity = new Entity();
entityManager.addEntity(entity);
```

or both in one step with `EntityManager.createEntity()`:

```java
Entity entity = entityManager.createEntity();
```


## EntityManager

The entity manager is responsible for handling entities, their components and systems. It is essentially a database of all entities and systems and is the link between entities and their components.
You will usually want to create an instance of an EntityManager:

```java
EntityManager entityManager = new EntityManager();
```

...And call its `update()` function every tick. This updates all of the Systems registered with the Entity Manager.
Entities are not registered in the entity manager database at all unless they have components attached to them.

Because components aren't directly attached to entities (only through the entity manager), components become detached from an entity the moment it is removed from the manager. Thus, removed entities that are then re-added to the manager will no longer have their components unless you handle this yourself.

### deltatime

`EntityManager.update()` takes a parameter `deltatime` which is a measurement of time between frames. You can utilise this however you like for framerate independence.


## Component

Components are simply data holders and nothing more. You should inherit the `Component` class when writing components. Components should not contain logic and should only contain data.  
An example position component class in a 2d game could be:

```java
public class PositionComponent extends Component {
    public float x;
    public float y;
}
```

However you might prefer to use private attributes with getter/setter functions.

### Adding and Removing Components

You can add and remove components from entities dynamically through the Entity Manager:

```java
EntityManager entityManager = new EntityManager();
Entity entity = entityManager.createEntity();
entityManager.addComponentToEntity(entity, new PositionComponent());
entityManager.removeComponentFromEntity(entity, PositionComponent.class)
```

Notice that you must provide a `Component` instance when adding a component but only need provide a `Component` type when removing one.

## EntitySystem
Systems are for processing specific sets of entities. You should inherit the `EntitySystem` class when writing systems. The `update()` method of your system is called every tick and should perform your logic, usually iterating through a `Family`. A `Family` is essentially a set of `Entity` instances and should be assigned in the constructor.  
Usually in a System you want to only perform logic on entities that have a specific Component type or set of Component types. For example, a movement system might only affect entities that have a position component and a velocity component, we use families to hold these for us, and we can obtain a `Family` of entities by using `getFamily` from an `EntityManager` instance.
Then, you would want to work with the components of the entities, so use `EntityManager`'s `getComponentMap()` to get all components of a type so you can access a `Component` by using an `Entity` as the key.  
Here's how it might look:

```java
public class MovementSystem extends EntitySystem {
    HashMap<Entity, Component> positionComponentMap;
    HashMap<Entity, Component> velocityComponentMap;

    public MovementSystem (EntityManager entityManager) {
        // Get the Family of entities with position and velocity components:
        family = entityManager.getFamily(PositionComponent.class, VelocityComponent.class);
        // Get the component maps so we can access the components using the entity as a key:
        positionComponentMap = entityManager.getComponentMap(PositionComponent.class);
        velocityComponentMap = entityManager.getComponentMap(VelocityComponent.class);
    }

    @Override
    public void update(float deltatime) {
        for (Entity entity: family) {
            PositionComponent position = (PositionComponent) positionComponentMap.get(entity);
            VelocityComponent velocity = (VelocityComponent) velocityComponentMap.get(entity);
            position.x += velocity.x * deltatime;
            position.y += velocity.y * deltatime;
        }
    }
}
```

Then, you can add an instance of this system to the Entity Manager:

```java
MovementSystem movementSystem = new MovementSystem();
entityManager.addSystem(movementSystem);
```

Systems can also be removed dynamically:

```java
entityManager.removeSystem(movementSystem);
```

### System Priority

You may want to run systems in a given order, or prioritise some systems over others.

You can enable this behaviour when overriding the `EntitySystem` constructor. Systems have a priority of 0 by default. This means that when not specified, systems have the lowest possible priority, and are executed in the order they're added to the manager.

Interact with this how you choose. But, for example, if you want to mimic the default constructor, and take a dynamic priority parameter:

```java
public class MovementSystem extends EntitySystem {
    HashMap<Entity, Component> positionComponentMap;
    HashMap<Entity, Component> velocityComponentMap;

    public MovementSystem (EntityManager entityManager, int priority) {
        super(priority);
        // ...
    }
}
```

You can then instantiate your system with a given priority, `MovementSystem movementSystem = new MovementSystem(1)`

Systems with a higher priority will be executed first by the `EntityManager`.

### IteratorSystem

Most systems will involve iterating over a `Family`. To avoid redundant code, stup-ecs provides a handy utility class which will do this for you, called `IteratorSystem`.

In the constructor of your `System`, get the `Family` of entities as you normally would:

```java
Family family = entityManager.getFamily(PositionComponent, VelocityComponent);
```

Rather than overriding the `Update()` function, override `IteratorSystem`'s `Process()` function to do your logic and it will be applied to all entities in the family. The `MovementSystem` from before would look like this:

```java
public class MovementSystem extends IteratorSystem {
    HashMap<Entity, Component> positionComponentMap;
    EntityManager entityManager;

    public MovementSystem (EntityManager entityManager) {
        // Get the Family of entities with position and velocity components:
        family = entityManager.getFamily(family = entityManager.getFamily(PositionComponent.class, VelocityComponent.class));
        // Get the component maps so we can access the components using the entity as a key:        
        positionComponentMap = entityManager.getComponentMap(PositionComponent.class);
        velocityComponentMap = entityManager.getComponentMap(VelocityComponent.class);
    }

    @Override
    public void process(float deltatime, Entity entity) {
        PositionComponent position = (PositionComponent) positionComponentMap.get(entity);
        VelocityComponent velocity = (VelocityComponent) velocityComponentMap.get(entity);
        position.x += velocity.x * deltatime;
        position.y += velocity.y * deltatime;
    }
}
```

## EntityListener

Entity listeners can be registered with an `EntityManager` instance. Listeners get notified whenever an entity is added or removed from the manager. This is useful if you want to do something upon one of those events.

To write your own listener, implement the `EntityListener` interface. You must override `entityAdded` and `entityRemoved` but any that aren't required may not be implemented.

For the purpose of example the following class will print the entity object when it is removed from the manager.

```java
import java.util.HashMap;

public class PrintListener implements EntityListener {

    @Override
    public void entityAdded(Entity entity) {}

    @Override
    public void entityRemoved(Entity entity, HashMap<Class<? extends Component>, Component> components) {
        System.out.println(entity.toString());
    }
}
```

`entityAdded` takes the added entity as a parameter, whereas `entityRemoved` additionally takes the components that were attached to the entity in the system so that you can utilise them in your listener.

Currently, you must `import java.util.HashMap;` as `EntityRemoved` takes a `HashMap` as a parameter. This might be tweaked in future.

You must register a listener with an `ÈntityManager` instance for it to function. You can do so like this:

```java
EntityManager entityManager = new EntityManager();
PrintListener printListener = new PrintListener();
entityManager.addListener(printListener);
```

You can removed listeners from the manager, too:
```java
entityManager.removeListener(printListener);
```
