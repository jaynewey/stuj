package com.github.jaynewey.stuj;

import java.util.function.ToIntFunction;

/**
 * Abstract class for processing Entity instances.
 */
public abstract class EntitySystem {
    public Family family;
    private int priority = 0;

    public EntitySystem() {}

    public EntitySystem(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * The update method that is called every tick.
     * @param deltatime Time between frames. Can be used for framerate independence.
     */
    public abstract void update (float deltatime);
}
