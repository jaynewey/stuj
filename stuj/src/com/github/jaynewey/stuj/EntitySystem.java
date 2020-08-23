package com.github.jaynewey.stuj;

/**
 * Abstract class for processing Entity instances.
 */
public abstract class EntitySystem {
    public Family family;

    /**
     * The update method that is called every tick.
     * @param deltatime Time between frames. Can be used for framerate independence.
     */
    public abstract void update (float deltatime);
}
