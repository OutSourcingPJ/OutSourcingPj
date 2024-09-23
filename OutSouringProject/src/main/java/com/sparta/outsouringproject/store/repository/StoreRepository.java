package com.sparta.outsouringproject.store.repository;


import com.sparta.outsouringproject.user.entity.User;
import lombok.Getter;
import org.springframework.stereotype.Repository;


import com.sparta.outsouringproject.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.menuList WHERE s.storeId = :storeId AND s.storeStatus = false")
    Optional<Store> checkStore(@Param("storeId") Long storeId);

    // 모든 가게 반환 리스트 반환
    @Query("SELECT s FROM Store s WHERE s.storeStatus = false ORDER BY s.advertise DESC, s.storeId DESC")
    List<Store> findAllActiveStores();

    // 특정 이름을 가진 활성화된 가게 리스트 반환
    @Query("SELECT s FROM Store s WHERE s.storeStatus = false AND s.name LIKE %:name% ORDER BY s.advertise DESC, s.storeId DESC")
    List<Store> findStoresByName(@Param("name") String name);

    // 해당 사장님 가게 갯수 카운팅
    @Query("SELECT COUNT(s) FROM Store s WHERE s.user = :user AND s.storeStatus = false")
    Long countActiveStoresByUser(@Param("user") User user);

    // 가게 id로 가져올 때 orders 페치 조인
    @Query("SELECT s FROM Store s JOIN FETCH s.orders JOIN FETCH s.user WHERE s.storeId = :id")
    Optional<Store> findStoreWithOrdersAndUserById(@Param("id") Long id);
}
