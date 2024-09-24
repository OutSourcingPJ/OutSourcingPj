package com.sparta.outsouringproject.menu.controller;


import com.sparta.outsouringproject.common.annotation.Auth;
import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.menu.dto.MenuRequestDto;
import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.service.MenuService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;

    // 메뉴 생성
    @PostMapping
    public ResponseEntity<MenuResponseDto> createMenu(@Auth AuthUser authUser, @RequestBody MenuRequestDto requestDto) {
        try {
            MenuResponseDto menuResponseDto = menuService.saveMenu(authUser, requestDto);
            return new ResponseEntity<>(menuResponseDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException | AuthException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@Auth AuthUser authUser, @PathVariable Long menuId) {
        try {
            menuService.deleteMenu(authUser, menuId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException | AuthException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
