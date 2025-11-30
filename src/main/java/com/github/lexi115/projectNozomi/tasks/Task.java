package com.github.lexi115.projectNozomi.tasks;

/**
 * Interface for a task to execute.
 *
 * @author Lexi115
 * @since 1.0
 */
public interface Task {
    /**
     * Starts the task.
     *
     * @since 1.0
     */
    void start();

    /**
     * Stops the task.
     *
     * @since 1.0
     */
    void stop();

    /**
     * Restarts the task (if it's already started).
     *
     * @since 1.0
     */
    void restart();
}
