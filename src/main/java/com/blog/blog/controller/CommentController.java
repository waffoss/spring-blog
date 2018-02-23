package com.blog.blog.controller;

import com.blog.blog.bindingModel.CommentBindingModel;
import com.blog.blog.entity.Article;
import com.blog.blog.entity.Comment;
import com.blog.blog.entity.User;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.CommentRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    UserRepository userRepository;


    @PostMapping("/comment/add/{articleId}")
    public String addCommentProcess(@PathVariable Integer articleId, CommentBindingModel commentBindingModel){
        if(!this.articleRepository.exists(articleId)){
            return "redirect:/";
        }


        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(principal.getUsername());
            Article articleEnity =this.articleRepository.findOne(articleId);

            Comment comment = new Comment(commentBindingModel.getComment(),userEntity,articleEnity);
            this.commentRepository.saveAndFlush(comment);
            return "redirect:/article/" + articleId;
        }

        return "redirect:/article/" + articleId;
    }

}
