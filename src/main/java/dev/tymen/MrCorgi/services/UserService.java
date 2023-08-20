package dev.tymen.MrCorgi.services;

import dev.tymen.MrCorgi.models.entities.User;
import dev.tymen.MrCorgi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void create(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}
