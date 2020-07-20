import java.util.HashSet;
import java.util.Iterator;

public class Family implements Iterable<Entity> {
    /**
     * Data structure for tracking sets of entities that have specific Component types.
     */
    private HashSet<Entity> entities;

    public Family(HashSet<Entity> entities) {
        this.entities = entities;
    }

    public void setEntities(HashSet<Entity> entities) {
        /*
         * Sets this Family's entities
         */
        this.entities = entities;
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }
}
