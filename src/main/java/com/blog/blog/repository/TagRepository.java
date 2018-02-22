package com.blog.blog.repository;

import com.blog.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Integer> {

    Tag findByName(String name);
}
