package com.sparta.outsouringproject.store.repository;

import com.sparta.outsouringproject.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s WHERE s.storeId = :storeId AND s.storeStatus = false")
    Optional<Store> checkStore(@Param("storeId") Long storeId);

    // 모든 가게 반환 리스트 반환
    @Query("SELECT s FROM Store s WHERE s.storeStatus = true ORDER BY s.advertise DESC, s.storeId DESC")
    List<Store> findAllActiveStores();

    // 특정 이름을 가진 활성화된 가게 리스트 반환
    @Query("SELECT s FROM Store s WHERE s.storeStatus = true AND s.name LIKE %:name% ORDER BY s.advertise DESC, s.storeId DESC")
    List<Store> findStoresByName(@Param("name") String name);
}
