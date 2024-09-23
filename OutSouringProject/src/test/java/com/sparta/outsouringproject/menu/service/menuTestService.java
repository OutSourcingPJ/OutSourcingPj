package com.sparta.outsouringproject.menu.service;

import com.sparta.outsouringproject.menu.controller.MenuController;
import com.sparta.outsouringproject.menu.dto.MenuRequestDto;
import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.service.MenuService;
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


public class menuTestService {
    @InjectMocks
    private MenuController menuController;

    @Mock
    private MenuService menuService;

    private MenuRequestDto menuRequestDto;
    private MenuResponseDto menuResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        menuRequestDto = new MenuRequestDto("Pizza", "10000", 1L);
        menuResponseDto = new MenuResponseDto("Pizza", 10000L);
    }

    @Test
    void createMenu_ValidInput_ShouldReturnCreatedMenu() throws AuthException {
        when(menuService.saveMenu(any(String.class), any(MenuRequestDto.class))).thenReturn(menuResponseDto);

        ResponseEntity<MenuResponseDto> response = menuController.createMenu("Bearer owner-token", menuRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(menuResponseDto, response.getBody());
    }

    @Test
    void createMenu_InvalidInput_ShouldReturnBadRequest() throws AuthException {
        when(menuService.saveMenu(any(String.class), any(MenuRequestDto.class)))
                .thenThrow(new IllegalArgumentException("이미 존재하는 메뉴 이름입니다: Pizza"));

        ResponseEntity<MenuResponseDto> response = menuController.createMenu("Bearer owner-token", menuRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteMenu_ValidInput_ShouldReturnNoContent() throws AuthException {
        doNothing().when(menuService).deleteMenu(any(String.class), any(Long.class));

        ResponseEntity<Void> response = menuController.deleteMenu("Bearer owner-token", 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteMenu_InvalidMenuId_ShouldReturnBadRequest() throws AuthException {
        doThrow(new IllegalArgumentException("존재하지 않는 메뉴입니다.")).when(menuService).deleteMenu(any(String.class), any(Long.class));

        ResponseEntity<Void> response = menuController.deleteMenu("Bearer owner-token", 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
