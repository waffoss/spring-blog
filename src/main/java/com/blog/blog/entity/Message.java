package com.blog.blog.entity;

import javax.persistence.*;

@Entity
@Table(name = "messages")
public class Message {

    private Integer id;
    private User from;
    private User to;
    private String message;

    public Message() {
    }

    public Message(User from, User to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "from_id")
    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    @OneToOne
    @JoinColumn(name = "to_id")
    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    @Column(name = "message",nullable = false)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
