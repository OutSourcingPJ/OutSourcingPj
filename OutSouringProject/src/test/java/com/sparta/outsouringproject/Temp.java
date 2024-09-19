package com.sparta.outsouringproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.cart.repository.CartRepository;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.entity.repository.MenuRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
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
    @Test
    public void test() throws Exception {
        // given
        User user = new User();
        user = userRepository.save(user);

        Cart cart = cartRepository.findById(1L).get();
        Store store = new Store();
        store = storeRepository.save(store);
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "store", store);
        menu = menuRepository.save(menu);

        CartItem cartItem = new CartItem(cart, menu);
        ReflectionTestUtils.setField(cartItem, "cart", cart);
        ReflectionTestUtils.setField(cartItem, "menu", menu);

        cartItemRepository.save(cartItem);

        userRepository.delete(user);
        Menu menu1 = menuRepository.findById(1L)
            .get();
        assertEquals(false, menu1 == null);
        // when

        // then
    }
}
