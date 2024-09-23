package com.sparta.outsouringproject.order.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.entity.OrderItem;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void 가게의_주문_현황_조회() throws Exception {
        User user = new User("AAbb123!@#", UUID.randomUUID().toString().substring(0, 8), "test@email.com", Role.OWNER, false);
        user = userRepository.save(user);
        User user2 = new User("AAbb123!@#", UUID.randomUUID().toString().substring(0, 8), "test2@email.com", Role.OWNER, false);
        user2 = userRepository.save(user2);

        Store store = new Store();
        store = storeRepository.save(store);

        Menu menu = new Menu("test_1", null, 5000L);
        Menu menu2 = new Menu("test_2", null, 5000L);
        menu.relatedStore(store);
        menu2.relatedStore(store);
        menu = menuRepository.save(menu);
        menu2 = menuRepository.save(menu2);

        Order order = new Order(user, store, OrderStatus.WAITING);
        order = orderRepository.save(order);

        Order order2 = new Order(user2, store, OrderStatus.WAITING);
        order2 = orderRepository.save(order2);

        OrderItem oi = new OrderItem(order, menu, 1L, 5000L, 5000L);
        OrderItem oi2 = new OrderItem(order, menu, 1L, 8000L, 8000L);
        OrderItem oi3 = new OrderItem(order, menu, 1L, 10000L, 10000L);
        OrderItem oi4 = new OrderItem(order2, menu2, 1L, 15000L, 15000L);
        OrderItem oi5 = new OrderItem(order2, menu2, 2L, 15000L, 30000L);
        orderItemRepository.saveAll(List.of(oi, oi2, oi3, oi4,oi5));

        // when
        List<OrderItem> f = orderRepository.findAllOrderItemByStore(store);

        for (OrderItem orderItem : f) {
            System.out.println(orderItem.getId());
        }

        // then
        assertThat(store).isNotNull();
        assertThat(f).isNotEmpty();
        assertThat(f.size()).isEqualTo(5);
        assertThat(f).containsExactly(oi, oi2, oi3, oi4, oi5);
    }

    @Test
    public void 오더_아이디로_조회_성공() throws Exception {
        User user = new User("AAbb123!@#", UUID.randomUUID().toString().substring(0, 8), "test@email.com", Role.OWNER, false);
        user = userRepository.save(user);

        Store store = new Store();
        store = storeRepository.save(store);

        Order order = new Order(user, store, OrderStatus.WAITING);
        order = orderRepository.save(order);

        // when
        Order o = orderRepository.findByIdOrElseThrow(order.getId());

        // then
        assertThat(o).isNotNull();
        assertThat(o.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(o.getId()).isEqualTo(order.getId());
    }

    @Test
    public void 오더_아이디로_조회_실패() throws Exception {
        User user = new User("AAbb123!@#", UUID.randomUUID().toString().substring(0, 8), "test@email.com", Role.OWNER, false);
        user = userRepository.save(user);

        Store store = new Store();
        store = storeRepository.save(store);

        Order order = new Order(user, store, OrderStatus.WAITING);
        order = orderRepository.save(order);

        // when that
        assertThatThrownBy(() -> orderRepository.findByIdOrElseThrow(Long.MAX_VALUE)).isInstanceOf(
            InvalidDataAccessApiUsageException.class);
    }
}