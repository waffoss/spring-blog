package com.blog.blog.controller;

import com.blog.blog.entity.Tag;
import com.blog.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TagController {
    @Autowired
    TagRepository tagRepository;

    @GetMapping("/tag/{name}")
    public String articlesWithTag(Model model, @PathVariable String name){
        Tag tag = this.tagRepository.findByName(name);
        if(tag == null){
            return "redirect:/";
        }

        model.addAttribute("tag",tag);
        model.addAttribute("view","tag/articles");

        return "base-layout";
    }


}
