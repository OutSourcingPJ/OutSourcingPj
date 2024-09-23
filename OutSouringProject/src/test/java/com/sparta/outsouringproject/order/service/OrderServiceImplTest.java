package com.sparta.outsouringproject.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.common.exceptions.AccessDeniedException;
import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.notification.service.OrderStatusTracker;
import com.sparta.outsouringproject.order.dto.OrderCreateResponseDto;
import com.sparta.outsouringproject.order.dto.OrderItemInfo;
import com.sparta.outsouringproject.order.dto.OrderStatusChangeRequestDto;
import com.sparta.outsouringproject.order.dto.OrderStatusResponseDto;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.entity.OrderItem;
import com.sparta.outsouringproject.order.repository.OrderItemRepository;
import com.sparta.outsouringproject.order.repository.OrderRepository;
import com.sparta.outsouringproject.statistics.repository.OrderHistoryRepository;
import com.sparta.outsouringproject.store.dto.CreateStoreRequestDto;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.time.LocalTime;
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
class OrderServiceImplTest {


    @Mock private  OrderRepository orderRepository;
    @Mock private  OrderItemRepository orderItemRepository;
    @Mock private  UserRepository userRepository;
    @Mock private  MenuRepository menuRepository;
    @Mock private  StoreRepository storeRepository;
    @Mock private  CartItemRepository cartItemRepository;
    @Mock private  OrderHistoryRepository orderHistoryRepository;
    @Mock private  OrderStatusTracker orderStatusTracker;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Nested
    class 주문_생성 {
        @Test
        public void 주문_생성_시_장바구니가_비어있다면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(authUser);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(new ArrayList<>());

            assertThatThrownBy(() -> orderService.createOrder(authUser)).isInstanceOf(
                InvalidRequestException.class).hasMessage("장바구니에 상품이 존재하지 않습니다.");
        }

        @Test
        public void 주문_생성_시_가게가_존재하지_않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(authUser);
            Cart cart = new Cart();
            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            CartItem cartItem = new CartItem(1L, 5000L, cart, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.createOrder(authUser)).isInstanceOf(
                InvalidRequestException.class).hasMessage("존재하지 않는 가게입니다.");
        }

        @Test
        public void 주문가능시간에_주문하지_않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(authUser);
            Cart cart = new Cart();
            ReflectionTestUtils.setField(cart, "id", 1L);

            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            CartItem cartItem = new CartItem(1L, 5000L, cart, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.of(store));

            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().plusHours(2));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().plusHours(3));

            // when then
            // 오픈 전 주문
            assertThatThrownBy(() -> orderService.createOrder(authUser)).isInstanceOf(
                InvalidRequestException.class).hasMessage("주문 가능 시간이 아닙니다.");

            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().minusHours(3));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().minusHours(2));
            // 마감 후 주문
            assertThatThrownBy(() -> orderService.createOrder(authUser)).isInstanceOf(
                InvalidRequestException.class).hasMessage("주문 가능 시간이 아닙니다.");
        }

        @Test
        public void 장바구니아이템의_유저와_로그인한_유저가_다르면_AccessDeniedException이_발생한다() throws Exception {
            // given
            User authUser = User.fromAuthUser( new AuthUser(1L, "aaa@mail.com", Role.USER));
            Cart user_cart = new Cart();
            ReflectionTestUtils.setField(authUser,"cart", user_cart);
            ReflectionTestUtils.setField(user_cart, "id", 1L);
            ReflectionTestUtils.setField(user_cart,"user", authUser);

            User user2 = User.fromAuthUser(new AuthUser(2L, "aaa@mail.com", Role.USER));

            Cart user2_cart = new Cart();
            ReflectionTestUtils.setField(user2_cart, "id", 2L);
            ReflectionTestUtils.setField(user2_cart, "user", user2);
            ReflectionTestUtils.setField(user2, "cart", user2_cart);

            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            CartItem cartItem = new CartItem(1L, 2000L, user2_cart, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(authUser);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.of(store));

            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().minusHours(2));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().plusHours(3));

            // when then
            assertThatThrownBy(() -> orderService.createOrder(new AuthUser(1L, "aaa@mail.com", Role.USER))).isInstanceOf(
                AccessDeniedException.class).hasMessage("요청한 장바구니는 현재 유저의 장바구니가 아닙니다.");
        }

        @Test
        public void 장바구니의_총_금액이_0보다_작으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth =  new AuthUser(1L, "aaa@mail.com", Role.USER);
            User authUser = User.fromAuthUser(auth);
            Cart user_cart = new Cart();
            ReflectionTestUtils.setField(authUser,"cart", user_cart);
            ReflectionTestUtils.setField(user_cart, "id", 1L);
            ReflectionTestUtils.setField(user_cart,"user", authUser);

            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            CartItem cartItem = new CartItem(1L, -5000L, user_cart, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(authUser);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.of(store));

            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().minusHours(2));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().plusHours(3));

            // when then
            assertThatThrownBy(() -> orderService.createOrder(auth)).isInstanceOf(
                InvalidRequestException.class).hasMessage("총 금액은 0보다 크거나 같아야 합니다.");
        }

        @Test
        public void 장바구니의_총_금액이_가게의_최소_주문액보다_작으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth =  new AuthUser(1L, "aaa@mail.com", Role.USER);
            User authUser = User.fromAuthUser(auth);
            Cart user_cart = new Cart();
            ReflectionTestUtils.setField(authUser,"cart", user_cart);
            ReflectionTestUtils.setField(user_cart, "id", 1L);
            ReflectionTestUtils.setField(user_cart,"user", authUser);

            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            ReflectionTestUtils.setField(store, "orderAmount", 2000d);
            CartItem cartItem = new CartItem(1L,1000L,  user_cart, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(authUser);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.of(store));

            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().minusHours(2));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().plusHours(3));

            // when then
            assertThatThrownBy(() -> orderService.createOrder(auth)).isInstanceOf(
                InvalidRequestException.class).hasMessage("주문 금액이 최소 주문 금액보다 낮습니다.");
        }

        @Test
        public void 메뉴가_존재하지_않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth =  new AuthUser(1L, "aaa@mail.com", Role.USER);
            User authUser = User.fromAuthUser(auth);
            Cart user_cart = new Cart();
            ReflectionTestUtils.setField(authUser,"cart", user_cart);
            ReflectionTestUtils.setField(user_cart, "id", 1L);
            ReflectionTestUtils.setField(user_cart,"user", authUser);

            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            ReflectionTestUtils.setField(store, "orderAmount", 2000d);
            CartItem cartItem = new CartItem(1L,2000L, user_cart, menu);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(authUser);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.empty());

            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().minusHours(2));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().plusHours(3));

            // when then
            assertThatThrownBy(() -> orderService.createOrder(auth)).isInstanceOf(
                InvalidRequestException.class).hasMessage("존재하지 않는 메뉴입니다.");
        }

        @Test
        public void 주문_생성() throws Exception {
            // given
            AuthUser auth =  new AuthUser(1L, "aaa@mail.com", Role.USER);
            User authUser = User.fromAuthUser(auth);
            Cart user_cart = new Cart();
            ReflectionTestUtils.setField(authUser,"cart", user_cart);
            ReflectionTestUtils.setField(user_cart, "id", 1L);
            ReflectionTestUtils.setField(user_cart,"user", authUser);

            Menu menu = new Menu();
            Store store = new Store();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);
            ReflectionTestUtils.setField(store, "storeId", 1L);
            ReflectionTestUtils.setField(store, "orderAmount", 2000d);
            CartItem cartItem = new CartItem(1L, 2000L, user_cart, menu);
            Order order = new Order(authUser, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(authUser);
            given(cartItemRepository.findAllByCart_User(any(User.class))).willReturn(List.of(cartItem));
            given(storeRepository.findById(any())).willReturn(Optional.of(store));
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(orderRepository.save(any())).willReturn(order);
            ReflectionTestUtils.setField(store, "openTime", LocalTime.now().minusHours(2));
            ReflectionTestUtils.setField(store, "closeTime", LocalTime.now().plusHours(3));

            // when
            OrderCreateResponseDto res = orderService.createOrder(auth);
            assertThat(res).isNotNull();
            assertThat(res.getOrderId()).isEqualTo(order.getId());
            assertThat(res.getUserId()).isEqualTo(authUser.getId());
            assertThat(res.getStoreId()).isEqualTo(store.getStoreId());
            assertThat(res.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(res.getItems()).hasSize(1);
            // then
        }
    }

    @Nested
    class 주문_상태_변경 {

        @Test
        public void 주문_상태_변경_수락() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(auth);
            Store store  = new Store();
            ReflectionTestUtils.setField(store,"storeId", 1L);
            ReflectionTestUtils.setField(store,"user", user);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            Order order = new Order(user, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(orderRepository.findByIdOrElseThrow(anyLong())).willReturn(order);
            doNothing().when(orderStatusTracker).onOrderStatusChanged(any(), any());

            orderService.changeOrderStatus(auth, store.getStoreId(), order.getId(), new OrderStatusChangeRequestDto(OrderStatus.ACCEPTED));

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(orderStatusTracker).onOrderStatusChanged(any(), any());
        }

        @Test
        public void 주문_상태_변경_완료() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(auth);
            Store store  = new Store();
            ReflectionTestUtils.setField(store,"storeId", 1L);
            ReflectionTestUtils.setField(store,"user", user);

            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            Order order = new Order(user, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);
            OrderItem orderItem = new OrderItem(order, menu, 1L, 5000L, 5000L);

            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            given(orderRepository.findByIdOrElseThrow(anyLong())).willReturn(order);
            doNothing().when(orderStatusTracker).onOrderStatusChanged(any(), any());
            given(orderRepository.findAllOrderItemsByOrder(any())).willReturn(List.of(orderItem));

            orderService.changeOrderStatus(auth, store.getStoreId(), order.getId(), new OrderStatusChangeRequestDto(OrderStatus.COMPLETED));

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            verify(orderStatusTracker).onOrderStatusChanged(any(), any());
        }

        @Test
        public void 주문_상태_변경_시_가게정보가_없으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(auth);

            given(storeRepository.findById(anyLong())).willReturn(Optional.empty());
            // then
            assertThatThrownBy(() -> orderService.changeOrderStatus(auth, 1L, 1L,
                new OrderStatusChangeRequestDto(OrderStatus.COMPLETED)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("존재하지 않는 가게입니다.");
        }

        @Test
        public void 주문_상태_변경_시_가게사장정보가_없으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(auth);
            Store store  = new Store();
            ReflectionTestUtils.setField(store,"storeId", 1L);


            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);

            // then
            assertThatThrownBy(() -> orderService.changeOrderStatus(auth, 1L, 1L, new OrderStatusChangeRequestDto(OrderStatus.COMPLETED)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("가게의 사장 정보가 없습니다.");
        }

        @Test
        public void 주문_상태_변경_시_로그인한_유저가_가게의_사장이_아니면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser auth = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(auth);
            User user2 = User.fromAuthUser(auth);
            ReflectionTestUtils.setField(user,"id", 2L);
            Store store  = new Store();
            ReflectionTestUtils.setField(store,"user", user2);
            ReflectionTestUtils.setField(store,"storeId", 1L);


            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);

            // then
            assertThatThrownBy(() -> orderService.changeOrderStatus(auth, 1L, 1L, new OrderStatusChangeRequestDto(OrderStatus.COMPLETED)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("가게의 주인이 아닙니다.");
        }
    }

    @Nested
    class 주문_조회 {

        @Test
        public void 주문_조회_성공() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.OWNER);
            CreateStoreRequestDto requestDto = new CreateStoreRequestDto();

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", User.fromAuthUser(authUser));

            given(storeRepository.findStoreWithOrdersAndUserById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(User.fromAuthUser(authUser));

            // when
            List<OrderItemInfo> res = orderService.getAllOrdersByStoreId(authUser, 1L);

            // then
            assertThat(res).isEmpty();
        }

        @Test
        public void 주문_조회_성공2() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.OWNER);
            CreateStoreRequestDto requestDto = new CreateStoreRequestDto();

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", User.fromAuthUser(authUser));
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            given(storeRepository.findStoreWithOrdersAndUserById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(User.fromAuthUser(authUser));

            Order order = new Order(User.fromAuthUser(authUser), store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);
            OrderItem orderItem = new OrderItem(order, menu, 1L, 5000L, 5000L);
            OrderItem orderItem2 = new OrderItem(order, menu, 2L, 5000L, 10000L);
            OrderItem orderItem3 = new OrderItem(order, menu, 3L, 5000L, 15000L);
            OrderItem orderItem4 = new OrderItem(order, menu, 4L, 5000L, 20000L);

            given(orderRepository.findAllOrderItemByStore(any())).willReturn(List.of(orderItem, orderItem2, orderItem3, orderItem4));
            // when
            List<OrderItemInfo> res = orderService.getAllOrdersByStoreId(authUser, 1L);

            // then
            assertThat(res).hasSize(4);
            assertThat(res.get(0).getOrderId()).isEqualTo(order.getId());
            assertThat(res.get(0).getTotalPrice()).isEqualTo(5000L);

            assertThat(res.get(1).getOrderId()).isEqualTo(order.getId());
            assertThat(res.get(1).getTotalPrice()).isEqualTo(10000L);

            assertThat(res.get(2).getOrderId()).isEqualTo(order.getId());
            assertThat(res.get(2).getTotalPrice()).isEqualTo(15000L);

            assertThat(res.get(3).getOrderId()).isEqualTo(order.getId());
            assertThat(res.get(3).getTotalPrice()).isEqualTo(20000L);

        }


        @Test
        public void 주문_조회_시_가게_아이디가_존재하지않으면_InvalidRequestException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            CreateStoreRequestDto requestDto = new CreateStoreRequestDto();

            given(storeRepository.findStoreWithOrdersAndUserById(anyLong())).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(()-> orderService.getAllOrdersByStoreId(authUser, 1L)).isInstanceOf(
                InvalidRequestException.class).hasMessage("존재하지 않는 가게입니다.");
        }

        @Test
        public void 주문_조회_시_가게사장과_로그인한_유저가_다르다면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.OWNER);
            AuthUser authUser2 = new AuthUser(2L, "aaa@mail.com", Role.OWNER);
            CreateStoreRequestDto requestDto = new CreateStoreRequestDto();

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", User.fromAuthUser(authUser2));

            given(storeRepository.findStoreWithOrdersAndUserById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(User.fromAuthUser(authUser));

            // when then
            assertThatThrownBy(() -> orderService.getAllOrdersByStoreId(authUser, 1L)).isInstanceOf(
                AccessDeniedException.class).hasMessage("해당 가게의 사장이 아닙니다.");
        }

        @Test
        public void 주문_조회_시_로그인한_유저의ROLE이_USER면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            CreateStoreRequestDto requestDto = new CreateStoreRequestDto();

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", User.fromAuthUser(authUser));

            given(storeRepository.findStoreWithOrdersAndUserById(anyLong())).willReturn(Optional.of(store));
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(User.fromAuthUser(authUser));

            // when then
            assertThatThrownBy(() -> orderService.getAllOrdersByStoreId(authUser, 1L)).isInstanceOf(
                AccessDeniedException.class).hasMessage("해당 가게의 사장님만 조회할 수 있습니다.");
        }
    }

    @Nested
    class 현재_주문_상태_조회 {

        @Test
        public void 상태조회_성공_로그인_유저가_오너() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.OWNER);
            User user = User.fromAuthUser(authUser);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", user);
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            Order order = new Order(user, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(orderRepository.findByIdOrElseThrow(anyLong())).willReturn(order);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            // when
            OrderStatusResponseDto res = orderService.getCurrentOrderStatus(authUser, 1L);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getOrderStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        public void 상태조회_성공_로그인_유저가_유저() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            User user = User.fromAuthUser(authUser);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", user);
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            Order order = new Order(user, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(orderRepository.findByIdOrElseThrow(anyLong())).willReturn(order);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            // when
            OrderStatusResponseDto res = orderService.getCurrentOrderStatus(authUser, 1L);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getOrderStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        public void 상태조회_시_해당_가게의_사장이_아니면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.OWNER);
            AuthUser authUser2 = new AuthUser(2L, "aaa@mail.com", Role.OWNER);
            User user = User.fromAuthUser(authUser);
            User user2 = User.fromAuthUser(authUser2);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", user);
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            Order order = new Order(user, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(orderRepository.findByIdOrElseThrow(anyLong())).willReturn(order);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user2);
            // when then
            assertThatThrownBy(() -> orderService.getCurrentOrderStatus(authUser, 1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("해당 가게의 사장이 아닙니다.");
        }

        @Test
        public void 상태조회_시_로그인한_유저가_주문한_주문이_아니라면_AccessDeniedException이_발생한다() throws Exception {
            // given
            AuthUser authUser = new AuthUser(1L, "aaa@mail.com", Role.USER);
            AuthUser authUser2 = new AuthUser(2L, "aaa@mail.com", Role.OWNER);
            User user = User.fromAuthUser(authUser);
            User user2 = User.fromAuthUser(authUser2);

            Store store = new Store();
            ReflectionTestUtils.setField(store, "user", user);
            Menu menu = new Menu();
            ReflectionTestUtils.setField(menu, "menu_id", 1L);
            ReflectionTestUtils.setField(menu, "store", store);

            Order order = new Order(user2, store, OrderStatus.WAITING);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(orderRepository.findByIdOrElseThrow(anyLong())).willReturn(order);
            given(userRepository.findByIdOrElseThrow(anyLong())).willReturn(user);
            // when then
            assertThatThrownBy(() -> orderService.getCurrentOrderStatus(authUser, 1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("주문자가 아닙니다.");
        }
    }
}