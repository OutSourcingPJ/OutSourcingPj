package com.sparta.outsouringproject.cart.service;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.dto.CartItemListInfo;
import com.sparta.outsouringproject.cart.dto.CartItemUpdateRequestDto;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.user.entity.User;

public interface CartService {
    /**
     * 장바구니에 물품 추가
     *
     * @param auth
     * @param requestDto
     * @return
     */
    CartItemInfo addMenu(AuthUser auth, AddMenuRequestDto requestDto);

    /**
     * 장바구니에 해당 아이디 물품 수량 변경
     *
     * @param auth
     * @param cartItemId
     * @param requestDto
     * @return
     */
    CartItemInfo updateQuantity(AuthUser auth, Long cartItemId,
        CartItemUpdateRequestDto requestDto);

    /**
     * 유저의 장바구니에 들어있는 물품 단건 조회
     * @param user
     * @param cartItemId
     * @return
     */
    CartItemInfo getCartItem(AuthUser user, Long cartItemId);

    /**
     * 유저의 장바구니에 들어있는 물품 전체 조회
     * @param auth
     * @return
     */
    CartItemListInfo getCartItems(AuthUser auth);

    /**
     * 장바구니에 해당 아이디의 물품 삭제
     *
     * @param auth
     * @param itemId
     */
    void deleteItem(AuthUser auth, Long itemId);

    /**
     * 장바구니에 있는 모든 물품 삭제
     * @param auth
     */
    void deleteAllItems(AuthUser auth);
}
