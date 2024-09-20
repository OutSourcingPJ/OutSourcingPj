package com.sparta.outsouringproject.user.repository;

import com.sparta.outsouringproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByIdAndIsDeletedFalse(String id); // 삭제되지 않은 사용자 찾기
    Optional<User> findById(String id); // 사용자 찾기 (탈퇴 여부 무관)
}
