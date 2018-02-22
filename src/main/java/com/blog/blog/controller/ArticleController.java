package com.blog.blog.controller;

import com.blog.blog.bindingModel.ArticleBindingModel;
import com.blog.blog.entity.Article;
import com.blog.blog.entity.Category;
import com.blog.blog.entity.Tag;
import com.blog.blog.entity.User;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.CategoryRepository;
import com.blog.blog.repository.TagRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class ArticleController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        List<Category> categories = this.categoryRepository.findAll();

        model.addAttribute("categories",categories);
        model.addAttribute("view", "article/create");

        return "base-layout";
    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ArticleBindingModel articleBindingModel) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());
        Category categoryEnitity = this.categoryRepository.findOne(articleBindingModel.getCategoryId());
        HashSet<Tag> tagEnitiy = this.findTagsFromString(articleBindingModel.getTagString());

        Article articleEntity = new Article(articleBindingModel.getTitle(), articleBindingModel.getContent(), userEntity,categoryEnitity,tagEnitiy);

        this.articleRepository.saveAndFlush(articleEntity);

        return "redirect:/";

    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }


        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(principal.getUsername());
            model.addAttribute("user",userEntity);
        }

        Article article = this.articleRepository.findOne(id);

        model.addAttribute("article", article);

        model.addAttribute("view", "article/details");

        return "base-layout";
    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id, Model model) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }


        List<Category> categories = this.categoryRepository.findAll();



        Article article = this.articleRepository.findOne(id);

        String tagString = article.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "));

        if (!isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        model.addAttribute("categories",categories);
        model.addAttribute("article", article);
        model.addAttribute("tags",tagString);
        model.addAttribute("view", "article/edit");

        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, ArticleBindingModel articleBindingModel) {
        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        Category category = this.categoryRepository.findOne(articleBindingModel.getCategoryId());
        HashSet<Tag> tags = this.findTagsFromString(articleBindingModel.getTagString());

        if (!isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        article.setCategory(category);
        article.setContent(articleBindingModel.getContent());
        article.setTags(tags);
        article.setTitle(articleBindingModel.getTitle());


        this.articleRepository.saveAndFlush(article);
        return "redirect:/article/" + article.getId();
    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Integer id, Model model) {
        if (!this.articleRepository.exists(id)) {
            return "reditect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");

        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id) {
        if (!articleRepository.exists(id)) {
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);

        if (!isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        this.articleRepository.delete(article);

        return "redirect:/";
    }

    private Boolean isUserAuthorOrAdmin(Article article) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userEnity = this.userRepository.findByEmail(userDetails.getUsername());

        return userEnity.isAdmin() || userEnity.isAuthor(article);
    }

    private HashSet<Tag> findTagsFromString(String tagString){
        HashSet<Tag> tags = new HashSet<>();
        String[] tagNames = tagString.split(",\\s*");

        for (String tagName : tagNames) {
            Tag currentTag = this.tagRepository.findByName(tagName);

            if(currentTag == null){
                currentTag = new Tag(tagName);
                this.tagRepository.saveAndFlush(currentTag);
            }

            tags.add(currentTag);
        }

        return tags;
    }
}
