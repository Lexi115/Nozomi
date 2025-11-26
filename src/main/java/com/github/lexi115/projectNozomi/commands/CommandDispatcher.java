package com.github.lexi115.projectNozomi.commands;

public interface CommandDispatcher {
    void setup();
    void register(Commands object);
}
