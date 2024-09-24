package com.sparta.outsouringproject.user.repository;

import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    // ID로 사용자를 찾는 메소드
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    // 이메일로 사용자를 찾는 메소드 추가
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // 이메일로 사용자를 찾는 메소드 (탈퇴 상태와 관계없이)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.cart WHERE u.id = :id")
    Optional<User> findByIdWithCart(@Param("id") Long id);

    default User findByIdOrElseThrow(Long id) {
        return findByIdWithCart(id).orElseThrow(() -> new InvalidRequestException("존재하지 않는 유저입니다."));
    }
}
