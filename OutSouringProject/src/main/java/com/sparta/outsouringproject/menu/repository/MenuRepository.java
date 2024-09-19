package com.sparta.outsouringproject.menu.repository;

import com.sparta.outsouringproject.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByName(String menuName);
}
