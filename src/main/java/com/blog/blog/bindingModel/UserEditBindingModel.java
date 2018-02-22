package com.blog.blog.bindingModel;

import java.util.List;

public class UserEditBindingModel extends UserBindingModel{
    private List<Integer> roles;

    public UserEditBindingModel() {
    }

    public UserEditBindingModel(List<Integer> roles) {
        this.roles = roles;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }
}
