package com.blog.blog.controller;

import com.blog.blog.bindingModel.UserBindingModel;
import com.blog.blog.entity.Message;
import com.blog.blog.entity.Role;
import com.blog.blog.entity.User;
import com.blog.blog.repository.MessageRepository;
import com.blog.blog.repository.RoleRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@Controller
public class UserController {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("view","user/register");

        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel){
        if(!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())){
            return "redirect:/register";
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = new User(userBindingModel.getEmail(),userBindingModel.getFullName(),bCryptPasswordEncoder.encode(userBindingModel.getPassword()));

        Role userRole = this.roleRepository.findByName("ROLE_USER");
        user.addRole(userRole);
        this.userRepository.saveAndFlush(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("view","user/login");

        return "base-layout";
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth !=null){
            new SecurityContextLogoutHandler().logout(request,response,auth);
        }

        return "redirect:login?logout";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String myProfilePage(Model model){
        UserDetails principle = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userRepository.findByEmail(principle.getUsername());
        model.addAttribute("user",user);
        model.addAttribute("view","user/profile");

        return "base-layout";
    }

    @GetMapping("/profile/{id}")
    public String profilePage(Model model, @PathVariable Integer id){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        User user = this.userRepository.findOne(id);
        model.addAttribute("user",user);
        model.addAttribute("view","user/guest-profile");

        return "base-layout";
    }

    @GetMapping("/profile/messages")
    @PreAuthorize("isAuthenticated()")
    public String myMessages(Model model){
        UserDetails principle = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userRepository.findByEmail(principle.getUsername());

        HashSet<Message> messages = this.messageRepository.findByTo(user);
        model.addAttribute("messages",messages);
        model.addAttribute("view","message/message-list");

        return "base-layout";
    }

    @GetMapping("/follow/{id}")
    @PreAuthorize("isAuthenticated()")
    public String followProcess(@PathVariable Integer id){
        if (!this.userRepository.exists(id)){
            return "redirect:/";
        }

        UserDetails principle = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User follower = this.userRepository.findByEmail(principle.getUsername());
        User user = this.userRepository.findOne(id);

        Set<User> followers = user.getFollowers();
        Set<User> following = follower.getFollowing();

        if(followers.contains(follower)){
            followers.remove(follower);
        }else{
            followers.add(follower);
        }

        if(following.contains(user)){
            following.remove(user);
        }else{
            following.add(user);
        }

        user.setFollowers(followers);
        follower.setFollowing(following);
        this.userRepository.saveAndFlush(user);
        this.userRepository.saveAndFlush(follower);

        return "redirect:/profile/"+id;

    }

}
