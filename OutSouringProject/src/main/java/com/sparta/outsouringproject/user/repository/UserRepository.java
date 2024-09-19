package com.sparta.outsouringproject.user.repository;

import com.sparta.outsouringproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
