package com.ncc.employee_management.service;

import com.ncc.employee_management.entity.User;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Integer id);

//    List<User> searchUsersByFullname(String name);
}