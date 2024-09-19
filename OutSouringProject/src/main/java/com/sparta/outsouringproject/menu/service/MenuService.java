package com.sparta.outsouringproject.menu.service;

import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {
    private final StoreRepository storeRepository;
    public final MenuRepository menuRepository;



}
