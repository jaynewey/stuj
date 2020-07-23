public abstract class IteratorSystem extends EntitySystem {
    /*
    Utility class for automatically iterating through a family of entities.
     */

    @Override
    public void update(float deltatime) {
        for (Entity entity: family) {
            process(deltatime, entity);
        }
    }

    public abstract void process(float deltatime, Entity entity);
    /*
    The method that performs logic on an entity and its components.
     */
}
