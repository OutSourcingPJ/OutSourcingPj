package com.sparta.outsouringproject.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void 카트아이템_아이디로_찾기() throws Exception {
        // given
        Cart cart = new Cart();
        Menu menu = new Menu();
        CartItem cartItem = new CartItem(1L, 2000L, cart, menu);
        cartItem = cartItemRepository.save(cartItem);

        // when
        Optional<CartItem> res = cartItemRepository.findById(cartItem.getId());

        // then
        assertThat(res).isPresent();
        assertThat(res.get()).isSameAs(cartItem);
        assertThat(res.get().getId()).isEqualTo(cartItem.getId());
    }

    @Test
    public void 카트아이템_유저로_찾기() throws Exception {
        // given
        User user = new User("adf@!#ADS1", "asdf", "email@mail.com", Role.USER, false);
        user = userRepository.save(user);
        Cart cart = new Cart();
        ReflectionTestUtils.setField(cart, "user", user);
        cart = cartRepository.save(cart);
        ReflectionTestUtils.setField(user, "cart", cart);
        user = userRepository.save(user);

        Store store = new Store();
        store = storeRepository.save(store);

        Menu menu = new Menu("name", null, 5000L);
        ReflectionTestUtils.setField(menu, "store", store);

        menu = menuRepository.save(menu);

        CartItem cartItem = new CartItem(1L, 2000L, cart, menu);
        cartItem = cartItemRepository.save(cartItem);

        // when
        List<CartItem> res = cartItemRepository.findAllByCart_User(user);

        // then
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getId()).isEqualTo(cartItem.getId());
        assertThat(res.get(0).getMenu()).isSameAs(menu);
        assertThat(res.get(0).getCart()).isSameAs(cart);
        assertThat(res.get(0).getMenu().getStore()).isSameAs(store);
    }
}