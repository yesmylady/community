package com.example.community.controller;

import com.example.community.exception.CustomizeErrorCode;
import org.apache.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 因为前端访问controller层中不存在的路由时会自动重定向到"/error"路由，
 * 这个类专门处理路由到"/error"的请求（如404，500等都会定向到这里）
 *
 * 注意exception层中的CustomizeException是为了容纳(请求能访问controller层但处理时在service层报错的)异常，并交由advice层处理
 */
@Controller
public class CustomizeErrorController implements ErrorController {
    Logger logger  = Logger.getLogger(this.getClass());

    @RequestMapping("error")
    public ModelAndView error(HttpServletRequest request, Model model) {

        HttpStatus status = getHttpStatus(request);

        if (status.is4xxClientError()) {
            model.addAttribute("message", CustomizeErrorCode.ERROR404);
        } else if (status.is5xxServerError()) {
            model.addAttribute("message", CustomizeErrorCode.ERROR500);
        }

        return new ModelAndView("error");  // 前端也已经写好了error.html
    }

    public static HttpStatus getHttpStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
