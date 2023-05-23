package com.example.community.exception;

/**
 * 枚举类的构造方法默认为私有的，这是因为枚举类的实例是预定义的有限个数，不能在运行时动态创建。
 * 如果需要在枚举类中使用构造方法，可以使用 private 或 default 访问权限，且只能在枚举类中访问。
 *
 * 枚举常量放在一起，用逗号隔开，最后分号结束
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND("question don't exist!"),
    ERROR404("404啊他妈的"),
    ERROR500("500啊他妈的");


    private String message;

    CustomizeErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
