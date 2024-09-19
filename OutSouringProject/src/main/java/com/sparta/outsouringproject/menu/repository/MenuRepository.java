package com.sparta.outsouringproject.menu.repository;

import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    default Optional<Menu> findByName(String menuName) {
        return null;
    }

    List<Menu> findByStoreAndDeletedFalse(Store store);
}
