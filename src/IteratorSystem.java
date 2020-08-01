/**
 * Utility class for automatically iterating through a family of entities.
 */
public abstract class IteratorSystem extends EntitySystem {
    /**
     * Automatically iterates through the family of the system.
     * @param deltatime Time between frames. Can be used for framerate independence.
     */
    @Override
    public void update(float deltatime) {
        for (Entity entity: family) {
            process(deltatime, entity);
        }
    }

    /**
     * The method that performs logic on an entity and its components.
     * @param deltatime Time between frames. Can be used for framerate independence.
     * @param entity The Entity instance that is being processed.
     */
    public abstract void process(float deltatime, Entity entity);
}
