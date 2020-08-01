import java.util.HashSet;
import java.util.Iterator;

/**
 * Data structure for tracking sets of entities that have specific Component types.
 */
public class Family implements Iterable<Entity> {
    private HashSet<Entity> entities;

    public Family(HashSet<Entity> entities) {
        this.entities = entities;
    }

    /**
     * Sets the entities belonging to the Family.
     * @param entities  A set of Entity instances.
     */
    public void setEntities(HashSet<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }
}
