package com.sparta.outsouringproject.cart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.dto.CartItemListInfo;
import com.sparta.outsouringproject.cart.dto.CartItemUpdateRequestDto;
import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.cart.repository.CartRepository;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.exceptions.AccessDeniedException;
import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private StoreRepository storeRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Nested
    class 메뉴_추가 {

        @Test
        public void 가게가_존재하지_않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Store store = new Store();
            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 1L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.empty());
            // when then

            assertThatThrownBy(() -> cartService.addMenu(auth, dto))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("존재하지 않는 가게입니다.");
        }

        @Test
        public void 메뉴가_존재하지_않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Store store = new Store();
            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 1L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.empty());
            // when then

            assertThatThrownBy(() -> cartService.addMenu(auth, dto))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("존재하지 않는 메뉴입니다.");
        }

        @Test
        public void 장바구니가_존재하지_않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Store store = new Store();
            Menu menu = new Menu();
            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 1L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(cartRepository.findByUser(any())).willReturn(Optional.empty());
            // when then

            assertThatThrownBy(() -> cartService.addMenu(auth, dto))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("존재하지 않는 장바구니입니다.");
        }

        @Test
        public void 넣으려는_장바구니가_로그인한유저의_장바구니가_아니면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);
            User user2 = User.fromAuthUser(new AuthUser(2L, "asdfa@mail.com", Role.USER));

            Store store = new Store();
            Menu menu = new Menu();
            Cart cart = new Cart();
            ReflectionTestUtils.setField(cart,"user", user2);

            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 1L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(cartRepository.findByUser(any())).willReturn(Optional.of(cart));
            // when then

            assertThatThrownBy(() -> cartService.addMenu(auth, dto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("요청한 장바구니의 소유자가 아닙니다.");
        }

        @Test
        public void 메뉴추가_성공_장바구니에_물품이_있을_때_가게_아이디가_다르면_기존에_있던_물품은_전부_삭제_된다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "storeId", 1L);

            Store store2 = new Store();
            ReflectionTestUtils.setField(store2, "storeId", 2L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "test");
            ReflectionTestUtils.setField(menu, "menuPrice", 1000L);

            Menu menu2 = new Menu();
            ReflectionTestUtils.setField(menu2, "store", store2);
            ReflectionTestUtils.setField(menu2, "menu_id", 2L);
            ReflectionTestUtils.setField(menu2, "menuName", "test");
            ReflectionTestUtils.setField(menu2, "menuPrice", 1000L);

            Cart cart = new Cart();
            ReflectionTestUtils.setField(cart,"user", user);
            ReflectionTestUtils.setField(cart,"id", 1L);
            ReflectionTestUtils.setField(cart, "cartItems", new ArrayList<>());

            CartItem cartItem1 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem1, "id", 1L);

            CartItem cartItem2 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem2, "id", 1L);

            CartItem cartItem3 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem3, "id", 1L);

            CartItem cartItem4 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem4, "id", 1L);

            CartItem cartItem5 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem5, "id", 1L);

            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 2L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(cartRepository.findByUser(any())).willReturn(Optional.of(cart));
            given(cartItemRepository.save(any())).willReturn(cartItem1);

            // when
            CartItemInfo cartItemInfo = cartService.addMenu(auth, dto);
            assertThat(cartItemInfo.getCartId()).isEqualTo(cart.getId());
            assertThat(cartItemInfo.getCartItemId()).isEqualTo(1L);
            assertThat(cartItemInfo.getPrice()).isEqualTo(1000L);
            assertThat(cartItemInfo.getTotalPrice()).isEqualTo(1000);
        }

        @Test
        public void 메뉴추가_성공_장바구니에_물품이_있을_때_가게_아이디가_같으면_그대로_추가() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "storeId", 1L);

            Store store2 = new Store();
            ReflectionTestUtils.setField(store2, "storeId", 2L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "test");
            ReflectionTestUtils.setField(menu, "menuPrice", 1000L);

            Menu menu2 = new Menu();
            ReflectionTestUtils.setField(menu2, "store", store2);
            ReflectionTestUtils.setField(menu2, "menu_id", 2L);
            ReflectionTestUtils.setField(menu2, "menuName", "test");
            ReflectionTestUtils.setField(menu2, "menuPrice", 1000L);

            Cart cart = new Cart();
            ReflectionTestUtils.setField(cart,"user", user);
            ReflectionTestUtils.setField(cart,"id", 1L);
            ReflectionTestUtils.setField(cart, "cartItems", new ArrayList<>());

            CartItem cartItem1 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem1, "id", 1L);

            CartItem cartItem2 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem2, "id", 1L);

            CartItem cartItem3 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem3, "id", 1L);

            CartItem cartItem4 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem4, "id", 1L);

            CartItem cartItem5 = new CartItem(1L, 2000L, cart, menu);
            ReflectionTestUtils.setField(cartItem5, "id", 1L);

            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 1L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(cartRepository.findByUser(any())).willReturn(Optional.of(cart));
            given(cartItemRepository.save(any())).willReturn(cartItem1);

            // when
            CartItemInfo cartItemInfo = cartService.addMenu(auth, dto);
            assertThat(cartItemInfo.getCartId()).isEqualTo(cart.getId());
            assertThat(cartItemInfo.getCartItemId()).isEqualTo(1L);
            assertThat(cartItemInfo.getPrice()).isEqualTo(1000L);
            assertThat(cartItemInfo.getTotalPrice()).isEqualTo(1000);
        }

        @Test
        public void 메뉴추가_성공_장바구니에_물품이_없을_때_그대로_추가() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "storeId", 1L);

            Store store2 = new Store();
            ReflectionTestUtils.setField(store2, "storeId", 2L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "test");
            ReflectionTestUtils.setField(menu, "menuPrice", 1000L);

            Menu menu2 = new Menu();
            ReflectionTestUtils.setField(menu2, "store", store2);
            ReflectionTestUtils.setField(menu2, "menu_id", 2L);
            ReflectionTestUtils.setField(menu2, "menuName", "test");
            ReflectionTestUtils.setField(menu2, "menuPrice", 1000L);

            Cart cart = new Cart();
            ReflectionTestUtils.setField(cart,"user", user);
            ReflectionTestUtils.setField(cart,"id", 1L);
            ReflectionTestUtils.setField(cart, "cartItems", new ArrayList<>());

            Cart tempCart = new Cart();
            ReflectionTestUtils.setField(tempCart ,"user", user);
            ReflectionTestUtils.setField(tempCart ,"id", 1L);
            ReflectionTestUtils.setField(tempCart , "cartItems", new ArrayList<>());

            CartItem cartItem = new CartItem(1L, 2000L, tempCart, menu);
            ReflectionTestUtils.setField(cartItem, "id", 1L);

            AddMenuRequestDto dto = new AddMenuRequestDto();
            ReflectionTestUtils.setField(dto, "storeId", 1L);
            ReflectionTestUtils.setField(dto, "menuId", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(cartRepository.findByUser(any())).willReturn(Optional.of(cart));
            given(cartItemRepository.save(any())).willReturn(cartItem);

            // when
            CartItemInfo cartItemInfo = cartService.addMenu(auth, dto);
            assertThat(cartItemInfo.getCartId()).isEqualTo(tempCart.getId());
            assertThat(cartItemInfo.getCartItemId()).isEqualTo(1L);
            assertThat(cartItemInfo.getPrice()).isEqualTo(1000L);
            assertThat(cartItemInfo.getTotalPrice()).isEqualTo(1000);
        }
    }

    @Nested
    class 수량_업데이트 {

        @Test
        public void 요청한_장바구니의_소유자가_아니면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);
            User user2 = User.fromAuthUser(new AuthUser(2L, "email@email.com", Role.USER));

            Cart cart = new Cart(user);
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);

            CartItem cartItem = new CartItem(1L, 1000L, cart, menu);
            CartItemUpdateRequestDto requestDto = new CartItemUpdateRequestDto();
            ReflectionTestUtils.setField(requestDto, "quantity", 2L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user2);
            given(cartItemRepository.findCartItemByIdOrElseThrow(anyLong())).willReturn(cartItem);


            // when then
            assertThatThrownBy(()-> cartService.updateQuantity(auth, 1L, requestDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("요청한 장바구니의 소유자가 아닙니다.");
        }

        @Test
        public void 수량_변경_성공() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Cart cart = new Cart(user);
            ReflectionTestUtils.setField(cart, "id", 1L);
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "치킨");

            CartItem cartItem = new CartItem(1L, 1000L, cart, menu);
            ReflectionTestUtils.setField(cartItem, "id", 1L);

            CartItemUpdateRequestDto requestDto = new CartItemUpdateRequestDto();
            ReflectionTestUtils.setField(requestDto, "quantity", 2L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findCartItemByIdOrElseThrow(anyLong())).willReturn(cartItem);


            // when then
            CartItemInfo cartItemInfo = cartService.updateQuantity(auth, 1L, requestDto);
            assertThat(cartItemInfo.getCartId()).isEqualTo(1L);
            assertThat(cartItemInfo.getCartItemId()).isEqualTo(1L);
            assertThat(cartItemInfo.getPrice()).isEqualTo(1000L);
            assertThat(cartItemInfo.getQuantity()).isEqualTo(2L);
            assertThat(cartItemInfo.getTotalPrice()).isEqualTo(2000L);
        }
    }

    @Nested
    class 장바구니_단건_조회 {

        @Test
        public void 아이템들이_담긴_카트와_로그인_유저의_카트가_다르다면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            AuthUser auth2 = new AuthUser(2L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);
            User user2 = User.fromAuthUser(auth2);

            Cart cart = new Cart(user);
            Cart cart2 = new Cart(user2);
            ReflectionTestUtils.setField(cart, "id", 1L);
            ReflectionTestUtils.setField(cart2, "id", 2L);

            Menu menu = new Menu();
            CartItem cartItem = new CartItem(1L, 5000L, cart2, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findCartItemByIdOrElseThrow(anyLong())).willReturn(cartItem);
            // when then
            assertThatThrownBy(()->cartService.getCartItem(auth, 1L));
        }

        @Test
        public void 장바구니_단건_조회_성공() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Cart cart = new Cart(user);
            ReflectionTestUtils.setField(cart, "id", 1L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "치킨");

            CartItem cartItem = new CartItem(1L, 5000L, cart, menu);
            ReflectionTestUtils.setField(cartItem,"id", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findCartItemByIdOrElseThrow(anyLong())).willReturn(cartItem);
            // when then
            CartItemInfo cartItemInfo = cartService.getCartItem(auth, 1L);
            assertThat(cartItemInfo.getCartId()).isEqualTo(1L);
            assertThat(cartItemInfo.getCartItemId()).isEqualTo(1L);
            assertThat(cartItemInfo.getPrice()).isEqualTo(5000L);
            assertThat(cartItemInfo.getQuantity()).isEqualTo(1L);
            assertThat(cartItemInfo.getTotalPrice()).isEqualTo(5000L);
            assertThat(cartItemInfo.getMenuId()).isEqualTo(1L);
            assertThat(cartItemInfo.getMenuName()).isEqualTo("치킨");
        }
    }

    @Nested
    class 장바구니_다건_조회 {
        @Test
        public void 아이템들이_담긴_카트와_로그인_유저의_카트가_다르다면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            AuthUser auth2 = new AuthUser(2L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);
            User user2 = User.fromAuthUser(auth2);

            Cart cart = new Cart(user);
            Cart cart2 = new Cart(user2);
            ReflectionTestUtils.setField(cart, "id", 1L);
            ReflectionTestUtils.setField(cart2, "id", 2L);

            Menu menu = new Menu();
            CartItem cartItem = new CartItem(1L, 5000L, cart2, menu);
            List<CartItem> l = List.of(cartItem);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findAllByCart_User(any())).willReturn(l);
            // when then
            assertThatThrownBy(()->cartService.getCartItems(auth))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("요청한 장바구니의 소유자가 아닙니다.");
        }

        @Test
        public void 장바구니_다건_조회_성공() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Cart cart = new Cart(user);
            ReflectionTestUtils.setField(cart, "id", 1L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "치킨");

            CartItem cartItem = new CartItem(1L, 5000L, cart, menu);
            ReflectionTestUtils.setField(cartItem,"id", 1L);

            List<CartItem> l = List.of(cartItem);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findAllByCart_User(any())).willReturn(l);

            // when then
            CartItemListInfo cartItems = cartService.getCartItems(auth);
            assertThat(cartItems).isNotNull();
            assertThat(cartItems.getItems()).hasSize(1);
            assertThat(cartItems.getTotalPrice()).isEqualTo(5000L);
        }
    }

    @Nested
    class 장바구니_물품_삭제 {

        @Test
        public void 단건_삭제_시_요청한_장바구니의_소유자와_로그인한_유저가_다르다면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            AuthUser auth2 = new AuthUser(2L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);
            User user2 = User.fromAuthUser(auth2);

            Cart cart = new Cart(user);
            ReflectionTestUtils.setField(cart, "id", 1L);

            Cart cart2 = new Cart(user2);
            ReflectionTestUtils.setField(cart2, "id", 2L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "치킨");

            CartItem cartItem = new CartItem(1L, 5000L, cart2, menu);
            ReflectionTestUtils.setField(cartItem,"id", 1L);

            given(userRepository.findByIdOrElseThrow(any())).willReturn(user);
            given(cartItemRepository.findCartItemByIdOrElseThrow(any())).willReturn(cartItem);
            // when then
            assertThatThrownBy(()->cartService.deleteItem(auth, 1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("요청한 장바구니의 소유자가 아닙니다.");
            verify(cartItemRepository, times(0)).delete(any());
        }

        @Test
        public void 단건_삭제_성공() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            Cart cart = new Cart(user);
            ReflectionTestUtils.setField(cart, "id", 1L);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "menuName", "치킨");

            CartItem cartItem = new CartItem(1L, 5000L, cart, menu);
            ReflectionTestUtils.setField(cartItem,"id", 1L);

            given(userRepository.findByIdOrElseThrow(any())).willReturn(user);
            given(cartItemRepository.findCartItemByIdOrElseThrow(any())).willReturn(cartItem);

            // when
            cartService.deleteItem(auth, 1L);

            // then
            verify(cartItemRepository, times(1)).delete(any());
        }
    }

    @Nested
    class 장바구니_물품_전체_삭제 {

        @Test
        public void 다건_삭제_성공() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "email@email.com", Role.USER);
            User user = User.fromAuthUser(auth);

            given(userRepository.findByIdOrElseThrow(any())).willReturn(user);

            // when
            cartService.deleteAllItems(auth);

            // then
            verify(cartItemRepository, times(1)).deleteAllByCart_User(any());
        }
    }
}