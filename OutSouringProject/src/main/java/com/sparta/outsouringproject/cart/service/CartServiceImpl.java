package com.sparta.outsouringproject.cart.service;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.dto.CartItemListInfo;
import com.sparta.outsouringproject.cart.dto.CartItemUpdateRequestDto;
import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.cart.repository.CartItemRepository;
import com.sparta.outsouringproject.cart.repository.CartRepository;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.User;
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
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Override
    public CartItemInfo addMenu(User user, AddMenuRequestDto requestDto) {
        Store store = storeRepository.findById(requestDto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Menu menu = menuRepository.findById(requestDto.getMenuId())
            .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        // 장바구니에 물품이 있을 때
        if(!cart.getCartItems().isEmpty()){
            Long storeId = cart.getCartItems()
                .get(0)
                .getMenu()
                .getStore()
                .getStoreId();

            // 가게 아이디가 다르면 현재 물품은 전부 삭제
            if(!Objects.equals(storeId, requestDto.getStoreId())){
                deleteAllItems(user);
            }
        }

        long menuPrice = menu.getMenuPrice();

        CartItem cartItem = new CartItem(1L, menuPrice, cart, menu);
        cartItem = cartItemRepository.save(cartItem);
        return CartItemInfo.builder()
            .cartItemId(cartItem.getId())
            .price(menuPrice)
            .menuId(menu.getMenu_id())
            .menuName(menu.getMenuName())
            .quantity(1L)
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    @Override
    public CartItemInfo updateQuantity(User user, Long cartItemId,
        CartItemUpdateRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(cartItemId);

        Menu menu = cartItem.getMenu();
        Long menuId = menu.getMenu_id();
        String menuName = menu.getMenuName();

        cartItem.updateQuantity(requestDto.getQuantity());
        return CartItemInfo.builder()
            .cartItemId(cartItem.getId())
            .price(cartItem.getPrice())
            .menuId(menuId)
            .menuName(menuName)
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    @Override
    public CartItemInfo getCartItem(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(cartItemId);
        Menu menu = cartItem.getMenu();
        Long menuId = menu.getMenu_id();
        String menuName = menu.getMenuName();

        return CartItemInfo.builder()
            .cartItemId(cartItem.getId())
            .price(cartItem.getPrice())
            .menuId(menuId)
            .menuName(menuName)
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    @Override
    public CartItemListInfo getCartItems(User user) {
        List<CartItem> res = cartItemRepository.findAllByCart_User(user);
        List<CartItemInfo> list = res.stream()
            .map(x -> CartItemInfo.builder()
                .cartItemId(x.getId())
                .price(x.getPrice())
                .menuId(x.getMenu().getMenu_id())
                .menuName(x.getMenu().getMenuName())
                .quantity(x.getQuantity())
                .totalPrice(x.getTotalPrice())
                .build())
            .toList();

        long totalPrice = 0;
        for (CartItemInfo cartItemInfo : list) {
            totalPrice += cartItemInfo.getTotalPrice();
        }

        return new CartItemListInfo(list, totalPrice);
    }

    @Override
    public void deleteItem(User user, Long itemId) {
        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(itemId);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void deleteAllItems(User user) {
        cartItemRepository.deleteAllByCart_User(user);
    }
}
