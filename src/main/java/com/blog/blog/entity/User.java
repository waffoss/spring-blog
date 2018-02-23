package com.blog.blog.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    private Integer id;
    private  String email;
    private  String fullName;
    private String password;
    private Set<Role> roles;
    private Set<Article> articles;
    private Set<User> followers;
    private Set<User> following;


    public User(String email, String fullName, String password) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;

        this.roles = new HashSet<>();
        this.articles = new HashSet<>();
        this.followers = new HashSet<>();
    }

    public User() {
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_following")
    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_followers")
    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    @OneToMany(mappedBy = "author")
    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "email",unique = true,nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fullName",nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "password", length = 60,nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    @Transient
    public boolean isAdmin(){
        return this.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    @Transient
    public boolean isAuthor(Article article){
        return Objects.equals(this.getId(),article.getAuthor().getId());
    }
}
