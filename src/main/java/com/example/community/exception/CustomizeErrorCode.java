package com.example.community.exception;

/**
 * 枚举类的构造方法默认为私有的，这是因为枚举类的实例是预定义的有限个数，不能在运行时动态创建。
 * 如果需要在枚举类中使用构造方法，可以使用 private 或 default 访问权限，且只能在枚举类中访问。
 *
 * 枚举常量放在一起，用逗号隔开，最后分号结束
 * 枚举类中的成员(QUES.. ERROR..)可以有自己的属性(code和message)和方法，也可以重写枚举类的方法。
 * 枚举类的成员在使用时可以直接通过枚举类名称(Custom..(code,message))和成员名称(ERROR..(code,message))进行访问。
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001, "question don't exist!"),
    TARGET_PARA_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NOT_LOGIN(2003, "未登录"),
    SYS_ERROR(2004, "服务器出错"),
    TYPE_PARA_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "评论不存在于数据库！"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "兄弟你这是读谁的信息呢？"),
    NOTIFICATION_NOT_FOUND(2009, "没有通知！"),

    ERROR404(404, "404啊他妈的"),
    ERROR500(500, "500啊他妈的");


    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
