package com.example.chat.dao;

import com.example.chat.domain.User;
import com.example.chat.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDAO {

    @Autowired
    private UserRepository repository;

    public User insert(User auction) {
        return repository.save(auction);
    }

    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    public User findSingle(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
