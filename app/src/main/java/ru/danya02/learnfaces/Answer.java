package ru.danya02.learnfaces;

import java.io.Serializable;

public class Answer implements Serializable {
    public String name, path;

    enum states {CORRECT, INCORRECT, SKIPPED}

    states state;

    public Answer(String name, String path, states state) {
        this.state = state;
        this.name = name;
        this.path = path;
    }

    public Answer() {
    }
}
