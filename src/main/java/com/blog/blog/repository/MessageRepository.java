package com.blog.blog.repository;

import com.blog.blog.entity.Message;
import com.blog.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashSet;

public interface MessageRepository extends JpaRepository<Message,Integer>{
    HashSet<Message> findByTo(User to);
}
