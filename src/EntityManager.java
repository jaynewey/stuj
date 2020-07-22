import java.util.*;

public class EntityManager {
    private HashMap<Class<? extends Component>, HashMap<Entity, Component>> components = new HashMap<>();
    private HashMap<Entity, HashMap<Class<? extends Component>, Component>> entities = new HashMap<>();
    private HashMap<HashSet<Class<? extends Component>>, Family> families = new HashMap<>();
    private LinkedList<EntitySystem> systems = new LinkedList<>();

    public EntityManager () {
    }

    public Entity createEntity () {
        /*
          Creates a new Entity instance, adds it to the manager and returns it.
          @return A new Entity instance.
         */
        return addEntity(new Entity());
    }

    public Entity addEntity (Entity entity) {
        /*
          Adds an existing Entity instance to the manager and returns it.
          @return The added Entity instance.
         */
        if (!entities.containsKey(entity)) {
            entities.put(entity, new HashMap<>());
        }
        return entity;
    }

    public HashSet<Component> removeEntity (Entity entity) {
        return new HashSet<>();
    }

    public void addComponentToEntity (Entity entity, Component... components) {
        for (Component component: components) {
            Class<? extends Component> componentType = component.getClass();
            this.components.putIfAbsent(componentType, new HashMap<>(Map.of(entity, component)));
            entities.get(entity).putIfAbsent(componentType, component);
            // update all relevant families
            updateFamiliesWithComponentType(componentType);
        }
    }

    public void removeComponentFromEntity (Entity entity, Class<? extends Component> componentType) {
        components.get(componentType).remove(entity);
        entities.get(entity).remove(componentType);
        updateFamiliesWithComponentType(componentType);
    }

    public HashMap<Entity, Component> getComponentMap (Class<? extends Component> componentType) {
        return components.get(componentType);
    }

    public void addSystem(EntitySystem system) {
        systems.add(system);
    }

    public void removeSystem(EntitySystem system) {
        systems.remove(system);
    }

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

    public void update(float deltatime) {
        for (EntitySystem system: systems) {
            system.update(deltatime);
        }
    }
}
