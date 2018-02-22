package com.blog.blog.repository;

import com.blog.blog.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer>{
    Role findByName(String name);

}
