package com.blog.blog.repository;

import com.blog.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer>{
    User findByEmail(String email);

}
