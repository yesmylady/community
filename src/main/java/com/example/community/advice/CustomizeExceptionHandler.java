package com.example.community.advice;

import com.example.community.dto.ResultDTO;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeExceptionHandler {
    /**
     * 只会处理在controller层中方法抛出的异常，不会处理不存在的路由异常（不会自动跳到/error页面）
     * Throwable是父类，可用于接收自己继承出来的子类异常
     */
    @ResponseBody  // 本方法可返回ResultDTO或ModelAndView对象，此注解
    @ExceptionHandler(Exception.class)  // Controller层中抛出的所有类型的异常都会被这个方法拦截处理
    Object handle(HttpServletRequest request, Throwable e, Model model) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            // 若请求内容是json类型的，则我们也返回json错误，页面展示交给前端处理
            if (e instanceof CustomizeException) {  // 触发了我自定义的异常
                return ResultDTO.errorOf((CustomizeException)e);
            } else {                                // 触发了别的异常
                return ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
        } else {
            // 错误页面跳转
            if (e instanceof CustomizeException) {  // 触发了我自定义的异常
                model.addAttribute("message", e.getMessage());
            } else {                                // 触发了别的异常
                model.addAttribute("message", "我不到啊");
            }

            return new ModelAndView("error");
        }
    }


}
