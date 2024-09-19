package com.sparta.outsouringproject.user.repository;

import com.sparta.outsouringproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    default Optional<User> findByEmail(String email) {
        return null;
    }
}
