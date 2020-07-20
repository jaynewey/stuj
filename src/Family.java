import java.util.HashSet;
import java.util.Iterator;

public class Family implements Iterable<Entity> {
    private HashSet<Entity> entities = new HashSet<>();

    public Family(HashSet<Entity> entities) {
        this.entities = entities;
    }

    public void setEntities(HashSet<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }
}
