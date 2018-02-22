package com.blog.blog.controller;

import com.blog.blog.bindingModel.MessageBindingModel;
import com.blog.blog.entity.Message;
import com.blog.blog.entity.User;
import com.blog.blog.repository.MessageRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MessageController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/profile/message/send/{id}")
    @PreAuthorize("isAuthenticated()")
    public String sendMessage(Model model, @PathVariable Integer id){
        if(!this.userRepository.exists(id)){
            return "redirect:/";
        }

        User user = this.userRepository.findOne(id);
        model.addAttribute("user",user);
        model.addAttribute("view","message/send-message");

        return "base-layout";
    }

    @PostMapping("/profile/message/send/{id}")
    @PreAuthorize("isAuthenticated()")
    public String sendMessageProcess(@PathVariable Integer id, MessageBindingModel messageBindingModel){

        if(!this.userRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User to = this.userRepository.findOne(id);
        User from = this.userRepository.findByEmail(userDetails.getUsername());

        String messageString = messageBindingModel.getMessage();

        Message message = new Message(from,to,messageString);

        this.messageRepository.saveAndFlush(message);
        return "redirect:/";
    }

    @GetMapping("/profile/messages/{id}")
    @PreAuthorize("isAuthenticated()")
    public String messageDetails(@PathVariable Integer id,Model model){
        if (!this.messageRepository.exists(id)){
            return "redirect:/";
        }
        UserDetails principle = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userRepository.findByEmail(principle.getUsername());


        Message message = this.messageRepository.findOne(id);

        if(message.getTo().getId() != user.getId()){
            return "redirect:/";
        }

        model.addAttribute("message",message);
        model.addAttribute("view","message/details");

        return "base-layout";
    }
}
