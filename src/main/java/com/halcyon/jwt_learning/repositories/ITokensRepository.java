package com.halcyon.jwt_learning.repositories;

import com.halcyon.jwt_learning.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITokensRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByValue(String value);
}
