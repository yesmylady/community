package com.example.community.controller;


import com.example.community.dto.NotificationDTO;
import com.example.community.dto.PaginationDTO;
import com.example.community.model.User;
import com.example.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,
                          Model model,
                          HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        NotificationDTO notificationDTO = notificationService.read(id, user);

        if (notificationDTO != null) {

            return "redirect:/question/" + notificationDTO.getOuterId();
        }
        else
            return "redirect:/";
    }
}
