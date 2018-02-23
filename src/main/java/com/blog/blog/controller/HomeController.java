package com.blog.blog.controller;

import com.blog.blog.entity.Article;
import com.blog.blog.entity.Category;
import com.blog.blog.entity.User;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.CategoryRepository;
import com.blog.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model){
        List<Article> articles = this.articleRepository.findAll();
        List<Category> categories = this.categoryRepository.findAll();
        ArrayList<Article> articleList = new ArrayList<>(articles);
        ArrayList<Article> loggedArticleList = new ArrayList<>();

        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userEntity = this.userRepository.findByEmail(principal.getUsername());

            for (Article article : articles) {
                for (User follower : userEntity.getFollowing()) {
                    if(article.getAuthor().getId().equals(follower.getId())){
                        loggedArticleList.add(article);
                    }
                }
            }

            for (Article article : userEntity.getArticles()) {
                loggedArticleList.add(article);
            }

            model.addAttribute("loggedArticles",loggedArticleList);

        }

        articleList.sort(new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        });





        model.addAttribute("categories",categories);
        model.addAttribute("articles",articleList);
        model.addAttribute("view","home/index");

        return "base-layout";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model){
        model.addAttribute("view","error/403");

        return "base-layout";
    }

    @GetMapping("/category/{id}")
    public String listArticles(Model model, @PathVariable Integer id){

        if(!this.categoryRepository.exists(id)){
            return "redirect:/";
        }

        Category category = this.categoryRepository.findOne(id);
        Set<Article> articles = category.getArticles();

        model.addAttribute("category",category);
        model.addAttribute("articles",articles);
        model.addAttribute("view","home/list-articles");

        return "base-layout";
    }
}
