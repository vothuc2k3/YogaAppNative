package com.example.universalyoga.models;

import java.util.HashMap;
import java.util.Map;

public class ClassCategoryModel {
    private String id;
    private String name;
    private String description;

    public ClassCategoryModel(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("description", description);
        return map;
    }
}
