package com.example.community.enums;

public enum NotificationTypeEnum {
    REPLY_QUESTION(1, "回复问题"),
    REPLY_COMMENT(2, "回复评论"),
    ;

    private int type;
    private String name;

    NotificationTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public static String nameOfType(int type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType() == type) {
                return notificationTypeEnum.getName();
            }
        }

        return "没有这个notification type!";
    }
}
