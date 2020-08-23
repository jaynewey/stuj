package com.github.jaynewey.stuj;

import java.util.HashMap;

/**
 * Interface to listen to events broadcast by an EntityManager.
 */
public interface EntityListener {
    /**
     * This method gets called when an entity is added to a manager this listener is registered to.
     * @param entity The entity that was added.
     */
    void entityAdded(Entity entity);

    /**
     * This method gets called when an entity is removed from a manager this listener is registered to.
     * @param entity The entity that was removed.
     * @param components The map of components that were attached to the entity in the system.
     */
    void entityRemoved(Entity entity, HashMap<Class<? extends Component>, Component> components);
}
