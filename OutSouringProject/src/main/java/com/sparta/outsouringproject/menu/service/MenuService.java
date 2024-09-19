package com.sparta.outsouringproject.menu.service;

import com.sparta.outsouringproject.menu.dto.MenuRequestDto;
import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.dto.CreateStoreRequestDto;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import jakarta.security.auth.message.AuthException;
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

    @Transactional
    public MenuResponseDto saveMenu(String token, MenuRequestDto requestDto) throws AuthException {
        // 사장님 확인
        Store store = validateOwner(token, requestDto.getStoreId());

        // 메뉴 이름 중복 확인
        if (menuRepository.findByName(requestDto.getMenuName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 이름입니다: " + requestDto.getMenuName());
        }

        // 새로운 메뉴 생성
        Menu menu = new Menu(requestDto.getMenuName(), null, Long.parseLong(requestDto.getMenuPrice()));
        menuRepository.save(menu);

        return new MenuResponseDto(menu.getMenuName(), menu.getMenuPrice());
    }

    private Store validateOwner(String token, Store storeId) throws AuthException {
        // 토큰을 통해 사장님 정보를 가져오고, 사장님이 본인의 가게를 등록하려는지 확인하는 로직 필요
        Store store = storeRepository.findById(storeId.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // 사장님 확인 로직 예시
        if (!store.getStoreId().equals(token)) {
            throw new AuthException("권한이 없습니다.");
        }
        return store;
    }

    // 메뉴 삭제 메서드
    @Transactional
    public void deleteMenu(String token, Long menuId) throws AuthException {
        // 메뉴가 존재하는지 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // 해당 메뉴가 속한 가게의 사장님인지 확인
        Store store = validateOwner(token, menu.getStore());

        //메뉴 삭제 상태로 변경
        menu.deleteMenu();
        log.info("메뉴 '{}' 가 삭제되었습니다.", menu.getMenuName());
    }

}

