package com.sparta.outsouringproject.cart.controller;

import com.sparta.outsouringproject.cart.dto.AddMenuRequestDto;
import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.cart.dto.CartItemListInfo;
import com.sparta.outsouringproject.cart.dto.CartItemUpdateRequestDto;
import com.sparta.outsouringproject.cart.service.CartService;
import com.sparta.outsouringproject.common.dto.ResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니에 물품 추가
     */
    @PostMapping("/items")
    public ResponseEntity<ResponseDto<CartItemInfo>> addItem(@RequestBody AddMenuRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDto.of(HttpStatus.CREATED, cartService.addMenu(null, requestDto)));
    }

    /**
     * 장바구니에 해당 물품 수량 수정
     * @param itemId
     * @param request
     * @return
     */
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ResponseDto<CartItemInfo>> updateItem(@PathVariable("itemId") Long itemId,
        @RequestBody CartItemUpdateRequestDto request){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(HttpStatus.OK, cartService.updateQuantity(null, itemId, request)));
    }

    /**
     * 장바구니에 들어있는 물품 단건 조회
     * @return
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ResponseDto<CartItemInfo>> getItem(@PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok().body(ResponseDto.of(HttpStatus.OK,cartService.getCartItem(null, itemId)));
    }

    /**
     * 장바구니에 들어있는 물품 다건 조회
     * @return
     */
    @GetMapping("/items")
    public ResponseEntity<ResponseDto<CartItemListInfo>> getAllItems() {
        return ResponseEntity.ok().body(ResponseDto.of(HttpStatus.OK, cartService.getCartItems(null)));
    }

    /**
     * 장바구니에 있는 물품 삭제
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ResponseDto<Void>> deleteItem(@PathVariable("itemId") Long itemId) {
        cartService.deleteItem(null, itemId);
        return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "정상적으로 삭제되었습니다."));
    }

    /**
     * 장바구니에 있는 물품 전체 삭제
     */
    @DeleteMapping("/items")
    public ResponseEntity<ResponseDto<Void>> deleteAllItems() {
        cartService.deleteAllItems(null);
        return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "정상적으로 삭제되었습니다."));
    }
}
