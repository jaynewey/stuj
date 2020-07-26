import java.util.HashMap;

public interface EntityListener {
    void entityAdded(Entity entity);

    void entityRemoved(Entity entity, HashMap<Class<? extends Component>, Component> components);
}
