package com.sparta.outsouringproject.menu.entity;

import com.sparta.outsouringproject.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long menuId;


      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "store_id",nullable = false)
      private Store store;


    @Column(nullable = false, length = 50)
    private String menuName;

    @Column(nullable = false, length = 50)
    private Long menuPrice;

    //기본값은 false true 상태라면 삭제된 상태입니다.
    private Boolean deleted = false;


    public Menu(String menuName, Long menuId, Long menuPrice) {
        this.menuName = menuName;
        this.menuId = menuId;
        this.menuPrice = menuPrice;
    }

    public void deleteMenu() {
        this.deleted = true;
    }

    public void updateUserName(String menuName) {
        this.menuName = menuName;
    }




}
