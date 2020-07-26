import java.util.HashMap;

public interface EntityListener {
    void entityAdded(Entity entity);
    /*
    This method gets called when an entity is added to a manager this listener is registered to.
     */

    void entityRemoved(Entity entity, HashMap<Class<? extends Component>, Component> components);
    /*
    This method gets called when an entity is removed from a manager this listener is registered to.
     */
}
