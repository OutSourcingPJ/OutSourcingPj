package com.sparta.outsouringproject.menu.controller;

import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.menu.dto.MenuRequestDto;
import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.service.MenuService;
import com.sparta.outsouringproject.user.entity.Role;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MenuControllerTest {

    @InjectMocks
    private MenuController menuController;

    @Mock
    private MenuService menuService;

    private MenuRequestDto menuRequestDto;
    private MenuResponseDto menuResponseDto;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock request and response DTOs
        menuRequestDto = new MenuRequestDto("Pizza", "10000", 1L);
        menuResponseDto = new MenuResponseDto("Pizza", 10000L);

        // Mock AuthUser (representing the owner)
        authUser = new AuthUser(1L, "rlvuddl1234@naver.com",Role.OWNER);
    }

    @Test
    void 유효한_입력으로_메뉴_생성_시_생성된_메뉴를_반환해야_한다() throws AuthException {
        // Given
        when(menuService.saveMenu(any(AuthUser.class), any(MenuRequestDto.class))).thenReturn(menuResponseDto);

        // When
        ResponseEntity<MenuResponseDto> response = menuController.createMenu(authUser, menuRequestDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(menuResponseDto, response.getBody());
    }

    @Test
    void 잘못된_입력으로_메뉴_생성_시_잘못된_요청을_반환해야_한다() throws AuthException {
        // Given
        when(menuService.saveMenu(any(AuthUser.class), any(MenuRequestDto.class)))
                .thenThrow(new IllegalArgumentException("이미 존재하는 메뉴 이름입니다: Pizza"));

        // When
        ResponseEntity<MenuResponseDto> response = menuController.createMenu(authUser, menuRequestDto);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void 메뉴_삭제_시_유효한_입력이_주어지면_내용_없이_응답해야_한다() throws AuthException {
        // Given
        doNothing().when(menuService).deleteMenu(any(AuthUser.class), any(Long.class));

        // When
        ResponseEntity<Void> response = menuController.deleteMenu(authUser, 1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void 삭제할_메뉴_ID가_유효하지_않으면_잘못된_요청_응답을_반환해야_한다() throws AuthException {
        // Given
        doThrow(new IllegalArgumentException("존재하지 않는 메뉴입니다.")).when(menuService).deleteMenu(any(AuthUser.class), any(Long.class));

        // When
        ResponseEntity<Void> response = menuController.deleteMenu(authUser, 1L);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
