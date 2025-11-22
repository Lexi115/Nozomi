package com.github.lexi115.projectNozomi.tasks;

import org.quartz.Job;

public interface Task extends Job {
    void start();
    void stop();
    void restart();
}
