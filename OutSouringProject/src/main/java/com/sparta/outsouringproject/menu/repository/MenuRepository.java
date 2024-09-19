package com.sparta.outsouringproject.menu.repository;

import com.sparta.outsouringproject.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
