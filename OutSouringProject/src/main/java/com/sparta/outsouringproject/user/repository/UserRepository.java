package com.sparta.outsouringproject.user.repository;

import com.sparta.outsouringproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // ID로 사용자를 찾는 메소드
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    // 이메일로 사용자를 찾는 메소드 추가
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // 이메일로 사용자를 찾는 메소드 (탈퇴 상태와 관계없이)
    Optional<User> findByEmail(String email);
}
