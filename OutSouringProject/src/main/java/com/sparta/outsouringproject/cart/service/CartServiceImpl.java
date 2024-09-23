package com.sparta.outsouringproject.cart.service;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.dto.CartItemListInfo;
import com.sparta.outsouringproject.cart.dto.CartItemUpdateRequestDto;
import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.cart.repository.CartRepository;
import com.sparta.outsouringproject.common.annotation.Auth;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.exceptions.AccessDeniedException;
import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Override
    public CartItemInfo addMenu(AuthUser auth, AddMenuRequestDto requestDto) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        Store store = storeRepository.findById(requestDto.getStoreId())
            .orElseThrow(() -> new InvalidRequestException("존재하지 않는 가게입니다."));

        Menu menu = menuRepository.findById(requestDto.getMenuId())
            .orElseThrow(() -> new InvalidRequestException("존재하지 않는 메뉴입니다."));

        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new InvalidRequestException("존재하지 않는 장바구니입니다."));

        // 카트의 주인 유저가 아니면 throw
        if(cart.getUser() != user) {
            throw new AccessDeniedException("요청한 장바구니의 소유자가 아닙니다.");
        }


        // 장바구니에 물품이 있을 때
        if(!cart.getCartItems().isEmpty()){

            // 장바구니는 마지막 업데이트 시점으로부터 최대 하루만 유지
            // 물품이 있다면 삭제
            if(LocalDateTime.now().isAfter(cart.getUpdatedAt().plusDays(1))){
                cartItemRepository.deleteAllByCart_User(user);
            }


            Long storeId = cart.getCartItems()
                .get(0)
                .getMenu()
                .getStore()
                .getStoreId();

            // 가게 아이디가 다르면 현재 물품은 전부 삭제
            if(!Objects.equals(storeId, requestDto.getStoreId())){
                cartItemRepository.deleteAllByCart_User(user);
            }
        }

        long menuPrice = menu.getMenuPrice();

        CartItem cartItem = new CartItem(1L, menuPrice, cart, menu);
        cartItem = cartItemRepository.save(cartItem);
        return CartItemInfo.builder()
            .cartId(cart.getId())
            .cartItemId(cartItem.getId())
            .price(menuPrice)
            .menuId(menu.getMenu_id())
            .menuName(menu.getMenuName())
            .quantity(1L)
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    @Override
    public CartItemInfo updateQuantity(AuthUser auth, Long cartItemId,
        CartItemUpdateRequestDto requestDto) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(cartItemId);
        Cart cart = cartItem.getCart();

        if(cart != user.getCart()){
            throw new AccessDeniedException("요청한 장바구니의 소유자가 아닙니다.");
        }

        Menu menu = cartItem.getMenu();
        Long menuId = menu.getMenu_id();
        String menuName = menu.getMenuName();

        cartItem.updateQuantity(requestDto.getQuantity());
        return CartItemInfo.builder()
            .cartId(cart.getId())
            .cartItemId(cartItem.getId())
            .price(cartItem.getPrice())
            .menuId(menuId)
            .menuName(menuName)
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    @Override
    public CartItemInfo getCartItem(AuthUser auth, Long cartItemId) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(cartItemId);

        // 아이템들이 담긴 카트와 로그인 유저의 카트가 다르면 throw
        Cart cart = cartItem.getCart();
        if(cart != user.getCart()){
            throw new AccessDeniedException("요청한 장바구니의 소유자가 아닙니다.");
        }

        Menu menu = cartItem.getMenu();
        Long menuId = menu.getMenu_id();
        String menuName = menu.getMenuName();

        return CartItemInfo.builder()
            .cartId(cart.getId())
            .cartItemId(cartItem.getId())
            .price(cartItem.getPrice())
            .menuId(menuId)
            .menuName(menuName)
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    @Override
    public CartItemListInfo getCartItems(AuthUser auth) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        List<CartItem> res = cartItemRepository.findAllByCart_User(user);
        List<CartItemInfo> list = new ArrayList<>();

        for(CartItem item : res ) {
            Cart cart = item.getCart();
            Menu menu = item.getMenu();
            if(cart != user.getCart()) {
                throw new AccessDeniedException("요청한 장바구니의 소유자가 아닙니다.");
            }

            list.add(CartItemInfo.builder()
                .cartId(cart.getId())
                .cartItemId(item.getId())
                .price(item.getPrice())
                .menuId(menu.getMenu_id())
                .menuName(menu.getMenuName())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build());
        }

        long totalPrice = 0;
        for (CartItemInfo cartItemInfo : list) {
            totalPrice += cartItemInfo.getTotalPrice();
        }

        return new CartItemListInfo(list, totalPrice);
    }

    @Override
    public void deleteItem(AuthUser auth, Long itemId) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(itemId);

        if (cartItem.getCart() != user.getCart()) {
            throw new AccessDeniedException("요청한 장바구니의 소유자가 아닙니다.");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void deleteAllItems(AuthUser auth) {
        User user = userRepository.findByIdOrElseThrow(auth.getId());

        cartItemRepository.deleteAllByCart_User(user);
    }
}
