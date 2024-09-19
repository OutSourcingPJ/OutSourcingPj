package com.sparta.outsouringproject.store.repository;

import com.sparta.outsouringproject.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
