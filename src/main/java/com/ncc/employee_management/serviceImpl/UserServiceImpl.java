package com.ncc.employee_management.serviceImpl;

import com.ncc.employee_management.entity.User;
import com.ncc.employee_management.repository.UserRepository;
import com.ncc.employee_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        System.out.println("DELETE USER ID" + userId);
    }

//    @Override
//    public List<User> searchUsersByFullname(String name) {
//        return userRepository.findByFullnameContainingIgnoreCaseOrderByFullname(name);
//    }

}
