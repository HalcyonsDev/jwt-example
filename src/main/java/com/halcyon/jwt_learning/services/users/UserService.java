package com.halcyon.jwt_learning.services.users;

import com.halcyon.jwt_learning.models.User;
import com.halcyon.jwt_learning.repositories.IUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUsersRepository usersRepository;

    public User findByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    public User create(User user) {
        usersRepository.save(user);
        return user;
    }

}
