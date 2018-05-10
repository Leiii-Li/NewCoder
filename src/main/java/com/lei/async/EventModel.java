package com.lei.async;


import java.io.Serializable;
import java.util.*;

/**
 * Created by John on 2017/5/25.
 */
public class EventModel implements Serializable {
    private EventType type;
    //参与者id
    private String actorId;
    private int entityType;
    private String entityId;
    private String entityOwnerId;
    private Map<String, String> exts = new HashMap<>();

    public EventModel() {
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public String getActorId() {
        return actorId;
    }

    public EventModel setActorId(String actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(String entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "type=" + type +
                ", actorId='" + actorId + '\'' +
                ", entityType=" + entityType +
                ", entityId='" + entityId + '\'' +
                ", entityOwnerId='" + entityOwnerId + '\'' +
                ", exts=" + exts +
                '}';
    }
}
