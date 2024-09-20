package com.sparta.outsouringproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.cart.repository.CartRepository;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.statistics.entity.OrderHistory;
import com.sparta.outsouringproject.statistics.repository.OrderHistoryRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class Temp {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Test
    @Rollback(false)
    @Order(1)
    public void 유저_생성() throws Exception {
        User user = new User();
        userRepository.save(user);
    }

    @Test
    @Rollback(false)
    @Order(2)
    public void 가게_생성_메뉴_생성() throws Exception {
        // given
        Store store = new Store();
        storeRepository.save(store);
        Menu menu = new Menu();
        Menu menu2 = new Menu();
        ReflectionTestUtils.setField(menu, "store", store);
        ReflectionTestUtils.setField(menu2, "store", store);
        menuRepository.saveAll(List.of(menu, menu2));
        // when

        // then
    }

    @Test
    public void 장바구니에_추가() throws Exception {
        // given
        Menu menu = menuRepository.findById(1L).get();
        // CartItem cartItem = new CartItem()
        // when

        // then
    }

    @Test
    @Rollback(false)
    public void 오더_기록_임시_추가(){
        Random rand = new Random();
        for(long i = 0; i < 1000; ++i) {
            long price = rand.nextLong(5000L, 50000L);
            long quantity = rand.nextLong(1, 4);
            OrderHistory history = OrderHistory
                .builder()
                .soldDate(getRandomDateTime(LocalDateTime.now().minusDays(50), LocalDateTime.now().plusDays(50)))
                .soldPrice(price)
                .storeId(rand.nextLong(1, 15))
                .orderId(i + 1)
                .userId(rand.nextLong(1, 50))
                .quantity(quantity)
                .soldTotalPrice(price * quantity)
                .menuId(1l)
                .menuName("치킨")
                .build();
            orderHistoryRepository.save(history);
        }
    }

    @Test
    @Rollback(false)
    public void 가게_임시_추가(){
        String[] a = {"BBQ", "BHC","피나치공", "요아정", "화채꽃", "곱분이 곱창","스파게티 마스터"};
        Random rand = new Random();
        for(int i = 0; i<1000; ++i){
            Store store = new Store();
            ReflectionTestUtils.setField(store, "name", a[rand.nextInt(a.length)]);
            storeRepository.save(store);
        }
    }

    private static LocalDateTime getRandomDateTime(LocalDateTime start, LocalDateTime end) {
        // 시작 시간과 끝 시간의 차이 (초 단위로 계산)
        long startSeconds = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endSeconds = end.toEpochSecond(java.time.ZoneOffset.UTC);

        // 그 사이에서 랜덤 초 계산
        long randomSeconds = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds);

        // 랜덤 초를 LocalDateTime으로 변환
        return LocalDateTime.ofEpochSecond(randomSeconds, 0, java.time.ZoneOffset.UTC);
    }
}
