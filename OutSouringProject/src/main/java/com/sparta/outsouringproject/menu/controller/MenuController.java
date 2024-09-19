package com.sparta.outsouringproject.menu.controller;


import com.sparta.outsouringproject.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;
}
