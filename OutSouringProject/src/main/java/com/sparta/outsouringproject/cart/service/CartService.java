package com.sparta.outsouringproject.cart.service;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.dto.CartItemListInfo;
import com.sparta.outsouringproject.cart.dto.CartItemUpdateRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import java.util.List;

public interface CartService {
    /**
     * 장바구니에 물품 추가
     *
     * @param user
     * @param requestDto
     * @return
     */
    CartItemInfo addMenu(User user, AddMenuRequestDto requestDto);

    /**
     * 장바구니에 해당 아이디 물품 수량 변경
     *
     * @param user
     * @param cartItemId
     * @param requestDto
     * @return
     */
    CartItemInfo updateQuantity(User user, Long cartItemId,
        CartItemUpdateRequestDto requestDto);

    /**
     * 유저의 장바구니에 들어있는 물품 단건 조회
     * @param user
     * @param cartItemId
     * @return
     */
    CartItemInfo getCartItem(User user, Long cartItemId);

    /**
     * 유저의 장바구니에 들어있는 물품 전체 조회
     * @param user
     * @return
     */
    CartItemListInfo getCartItems(User user);

    /**
     * 장바구니에 해당 아이디의 물품 삭제
     *
     * @param user
     * @param itemId
     */
    void deleteItem(User user, Long itemId);

    /**
     * 장바구니에 있는 모든 물품 삭제
     * @param user
     */
    void deleteAllItems(User user);
}
