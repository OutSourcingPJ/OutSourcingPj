package com.sparta.outsouringproject.cart.service;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
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
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    /**
     * 장바구니에 물품 추가
     *
     * @param user
     * @param requestDto
     * @return
     */
    public CartItemInfo addMenu(User user, AddMenuRequestDto requestDto) {
        Store store = storeRepository.findById(requestDto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Menu menu = menuRepository.findById(requestDto.getMenuId())
            .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        Cart cart = cartRepository.findByUserId(user)
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

        // todo: 메뉴에 getPrice생기면 바꿔야함
        // long price = menu.getPrice();
        long menuPrice = 0;

        CartItem cartItem = new CartItem(1L, menuPrice, cart, menu);
        cartItem = cartItemRepository.save(cartItem);
        return CartItemInfo.builder()
            .cartItemId(cartItem.getId())
            .price(menuPrice)
            .quantity(1L)
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    /**
     * 장바구니에 해당 아이디 물품 수량 변경
     *
     * @param user
     * @param cartItemId
     * @param requestDto
     * @return
     */
    public CartItemInfo updateQuantity(User user, Long cartItemId,
        CartItemUpdateRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(cartItemId);
        cartItem.updateQuantity(requestDto.getQuantity());
        return CartItemInfo.builder()
            .cartItemId(cartItem.getId())
            .price(cartItem.getPrice())
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    /**
     * 유저의 장바구니에 들어있는 물품 단건 조회
     * @param user
     * @param cartItemId
     * @return
     */
    public CartItemInfo getCartItem(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(cartItemId);
        return CartItemInfo.builder()
            .cartItemId(cartItem.getId())
            .price(cartItem.getPrice())
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .build();
    }

    /**
     * 유저의 장바구니에 들어있는 물품 전체 조회
     * @param user
     * @return
     */
    public List<CartItemInfo> getCartItems(User user) {
        List<CartItem> res = cartItemRepository.findAllByCart_User(user);
        return res.stream()
            .map(x -> CartItemInfo.builder()
                .cartItemId(x.getId())
                .price(x.getPrice())
                .quantity(x.getQuantity())
                .totalPrice(x.getTotalPrice())
                .build())
            .toList();
    }

    /**
     * 장바구니에 해당 아이디의 물품 삭제
     *
     * @param user
     * @param itemId
     */
    public void deleteItem(User user, Long itemId) {
        CartItem cartItem = cartItemRepository.findCartItemByIdOrElseThrow(itemId);
        cartItemRepository.delete(cartItem);
    }

    /**
     * 장바구니에 있는 모든 물품 삭제
     * @param user
     */
    public void deleteAllItems(User user) {
        cartItemRepository.deleteAllByCart_User(user);
    }
}
