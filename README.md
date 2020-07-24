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
    + [IteratorSystem](#iteratorsystem)

## Usage

### Entity

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
