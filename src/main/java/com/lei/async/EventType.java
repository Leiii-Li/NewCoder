package com.lei.async;

/**
 * Created by John on 2017/5/25.
 */
public enum EventType {
    LIKE(0), COMMENT(1),LOGIN(2),MAIL(3),FOLLOW_USER(4),FOLLOW_QUESTION(5),LIKE_COMMENT(6);

    private int type;

    private EventType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
    }
