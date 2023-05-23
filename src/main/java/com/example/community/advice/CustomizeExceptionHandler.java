package com.example.community.advice;

import com.example.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeExceptionHandler {
    /**
     * 只会处理在controller层中方法抛出的异常，不会处理不存在的路由异常（出错默认跳转到/error）
     * Throwable可用于接收异常
     */
    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model) {
        if (e instanceof CustomizeException) {  // 触发了我自定义的异常
            model.addAttribute("message", e.getMessage());
        } else {                                // 触发了别的异常
            model.addAttribute("message", "我不到啊");
        }

        return new ModelAndView("error");
    }


}
