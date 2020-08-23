package com.github.jaynewey.stuj;

import java.util.*;

/**
 * Class responsible for handling Entities, their Components and Systems.
 */
public class EntityManager {
    private HashMap<Class<? extends Component>, HashMap<Entity, Component>> components = new HashMap<>();
    private HashMap<Entity, HashMap<Class<? extends Component>, Component>> entities = new HashMap<>();
    private HashMap<HashSet<Class<? extends Component>>, Family> families = new HashMap<>();
    private LinkedList<EntitySystem> systems = new LinkedList<>();
    private LinkedList<EntityListener> listeners = new LinkedList<>();

    public EntityManager () {
    }

    /**
     * Creates a new Entity instance, adds it to the manager and returns it.
     * @return A new Entity instance that has been added to the manager
     */
    public Entity createEntity () {
        return addEntity(new Entity());
    }

    /**
     * Adds an existing Entity instance to the manager and returns it.
     * @param entity The Entity instance to be added
     * @return The Entity instance that was added
     */
    public Entity addEntity (Entity entity) {
        if (!entities.containsKey(entity)) {
            entities.put(entity, new HashMap<>());
        }
        listeners.forEach((listener) -> listener.entityAdded(entity));
        return entity;
    }

    /**
     * Removes an Entity instance from the Entity Manager.
     * @param entity The Entity instance to be removed from the Entity Manager.
     * @return A set containing the removed entity's components
     */
    public HashSet<Component> removeEntity (Entity entity) {
        HashSet<Component> removedComponents = new HashSet<>();
        for (Class<? extends Component> componentType: components.keySet()) {
            if (components.get(componentType).containsKey(entity)) {
                removedComponents.add(components.get(componentType).remove(entity));
                updateFamiliesWithComponentType(componentType);
            }
        }
        listeners.forEach((listener) -> listener.entityRemoved(entity, entities.remove(entity)));
        return removedComponents;
    }

    /**
     * Returns True if the given Entity instance is in the entity manager, False if not.
     * @param entity The Entity instance to be checked.
     * @return A boolean representing whether the Entity was found or not.
     */
    public boolean entityExists(Entity entity) {
        return entities.containsKey(entity);
    }

    /**
     * Applies given Component instances to a given Entity instance.
     * @param entity The Entity to have components added to.
     * @param components The Component instances to be added to the entity.
     */
    public void addComponentToEntity (Entity entity, Component... components) {
        for (Component component: components) {
            Class<? extends Component> componentType = component.getClass();
            this.components.putIfAbsent(componentType, new HashMap<>(Map.of(entity, component)));
            entities.get(entity).putIfAbsent(componentType, component);
            // update all relevant families
            updateFamiliesWithComponentType(componentType);
        }
    }

    /**
     * Removes all Component instances of given Component type from given Entity instance.
     * @param entity The Entity to have components removed from.
     * @param componentType The Component type to removed from the Entity.
     */
    public void removeComponentFromEntity (Entity entity, Class<? extends Component> componentType) {
        components.get(componentType).remove(entity);
        entities.get(entity).remove(componentType);
        updateFamiliesWithComponentType(componentType);
    }

    /**
     * Returns map of key value pairs where Entity instances are the key and Component instances are the
     * values and the Component instances are of the given Component type.
     * @param componentType The type of Component
     * @return A HashMap of Entity instances to their Component instances.
     */
    public HashMap<Entity, Component> getComponentMap (Class<? extends Component> componentType) {
        return components.get(componentType);
    }

    /**
     * Adds given System instances to the Entity Manager.
     * @param system The System instance to add to the Entity Manager.
     */
    public void addSystem(EntitySystem system) {
        systems.add(system);
    }

    /**
     * Removes given System instance from the Entity Manager.
     * @param system The System instance to remove from the Entity Manager.
     */
    public void removeSystem(EntitySystem system) {
        systems.remove(system);
    }

    /**
     * Returns the Family of entities that have all of the given Component types.
     * @param componentTypes A Set of Component types that you want the Family for.
     * @return The Family of entities that have all of the requested Component types.
     */
    public Family getFamily(HashSet<Class<? extends Component>> componentTypes) {
        if (families.containsKey(componentTypes)) {
            return families.get(componentTypes);
        }
        HashSet<Entity> entities = new HashSet<>();
        for (Class<? extends Component> componentType: componentTypes) {
            if (entities.isEmpty()) {
                entities.addAll(components.get(componentType).keySet());
            } else {
                entities.retainAll(components.get(componentType).keySet());
            };
        }
        Family family = new Family(entities);
        families.put(componentTypes, family);
        return family;
    }

    /**
     * Returns the Family of entities that have all of the given Component types.
     * @param componentTypes A list of Component types that you want the Family for.
     * @return The Family of entities that have all of the requested Component types.
     */
    @SafeVarargs
    public final Family getFamily(Class<? extends Component>... componentTypes) {
        return getFamily(new HashSet<>(Arrays.asList(componentTypes)));
    }

    private void updateFamily(HashSet<Class<? extends Component>> componentTypes) {
        HashSet<Entity> entities = new HashSet<>();
        for (Class<? extends Component> componentType: componentTypes) {
            if (entities.isEmpty()) {
                entities.addAll(components.get(componentType).keySet());
            } else {
                entities.retainAll(components.get(componentType).keySet());
            };
        }
        families.get(componentTypes).setEntities(entities);
    }

    private void updateFamiliesWithComponentType(Class<? extends Component> componentType) {
        for (HashSet<Class<? extends Component>> family: families.keySet()) {
            if (family.contains(componentType)) {
                updateFamily(family);
            }
        }
    }

    /**
     * Adds a given EntityListener instance to the Entity Manager.
     * @param listener The EntityListener instance to add to the Entity Manager.
     */
    public void addListener(EntityListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a given EntityListener instance from the Entity Manager.
     * @param listener The EntityListener instance to remove from the Entity Manager.
     */
    public void removeListener(EntityListener listener) {
        listeners.remove(listener);
    }

    /**
     * Updates all systems in the database by calling their update functions. Should be called every tick.
     * @param deltatime Time between frames. Can be used for framerate independence.
     */
    public void update(float deltatime) {
        for (EntitySystem system: systems) {
            system.update(deltatime);
        }
    }
}
