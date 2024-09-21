package com.sparta.outsouringproject.menu.entity;


//import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.store.entity.Store;
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
    private Long menu_id;


      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "store_id",nullable = false)
      private Store store;


    @Column(nullable = false, length = 50)
    private String menuName;

    @Column(nullable = false, length = 50)
    private Long menuPrice;

    //기본값은 false true 상태라면 삭제된 상태입니다.
    private Boolean deleted = false;


    public Menu(String menuName, Long menu_id, Long menuPrice) {
        this.menuName = menuName;
        this.menu_id = menu_id;
        this.menuPrice = menuPrice;
    }

    public void deleteMenu() {
        this.deleted = true;
    }

    public void updateUserName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * 생성자: 정재호
     * 생성 이유: 메뉴 Mock 데이터 만들 때 Store 지정해줘야 해서 생성
     * @param store
     */
    public void relatedStore(Store store){
        this.store = store;
        store.getMenuList().add(this);
    }
}
