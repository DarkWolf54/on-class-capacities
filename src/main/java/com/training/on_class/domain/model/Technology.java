package com.training.on_class.domain.model;

public class Technology {

    private final Long id;
    private final String name;

    public Technology(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
