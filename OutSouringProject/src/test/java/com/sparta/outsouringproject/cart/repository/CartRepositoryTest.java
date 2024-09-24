package com.sparta.outsouringproject.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void 유저의_장바구니_찾기 () throws Exception {
        // given
        User user = new User("asdASD!@#2123", UUID.randomUUID()
            .toString(), "asdf@mail.com", Role.USER,false);
        Cart cart = new Cart(user);
        ReflectionTestUtils.setField(user, "cart", cart);
        user = userRepository.save(user);
        cart = cartRepository.save(cart);

        // when
        Optional<Cart> byUser = cartRepository.findByUser(user);

        // then
        assertThat(byUser).isPresent();
        assertThat(byUser.get()).isEqualTo(cart);
    }
}