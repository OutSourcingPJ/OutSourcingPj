package com.sparta.outsouringproject.menu.service;

import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.menu.dto.MenuRequestDto;
import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.menu.repository.MenuRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {
    private final StoreRepository storeRepository;
    public final MenuRepository menuRepository;




    @Transactional
    public MenuResponseDto saveMenu(AuthUser auth, MenuRequestDto requestDto) throws AuthException {
        // 사장님 확인
        Store store = validateOwner(auth, requestDto.getStoreId());


        // 메뉴 이름 중복 확인
        if (menuRepository.findByMenuName(requestDto.getMenuName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 이름입니다: " + requestDto.getMenuName());
        }


        // 새로운 메뉴 생성
        Menu menu = new Menu(requestDto.getMenuName(), Long.parseLong(requestDto.getMenuPrice()));

        menu.relatedStore(store);
        menuRepository.save(menu);

        return new MenuResponseDto(menu.getMenuName(), menu.getMenuPrice());
    }

    // 메뉴 리스트 조회 메서드
    public List<MenuResponseDto> getMenuList(Long storeId) {
        // 1. 가게가 존재하는지 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // 2. 해당 가게의 삭제되지 않은 메뉴 조회
        List<Menu> menus = menuRepository.findByStoreAndDeletedFalse(store);

        // 3. 조회된 메뉴들을 MenuResponseDto로 변환하여 반환
        return menus.stream()
                .map(menu -> new MenuResponseDto(menu.getMenuName(), menu.getMenuPrice()))
                .collect(Collectors.toList());
    }

    private Store validateOwner(AuthUser auth, Long storeId) throws AuthException {
        // 토큰을 통해 사장님 정보를 가져오고, 사장님이 본인의 가게를 등록하려는지 확인하는 로직 필요
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // 사장님 확인 로직 예시
        if (!store.getUser().getRole().equals(auth.getRole())) {
            throw new AuthException("권한이 없습니다.");
        }

        return store;
    }

    // 메뉴 삭제 메서드
    @Transactional
    public void deleteMenu(AuthUser auth, Long menuId) throws AuthException {
        // 메뉴가 존재하는지 확인
        Menu menus = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // 해당 메뉴가 속한 가게의 사장님인지 확인
        Store store = validateOwner(auth, menus.getStore().getStoreId() );
        menus.relatedStore(store);
        //메뉴 삭제 상태로 변경
        menus.deleteMenu();
        log.info("메뉴 '{}' 가 삭제되었습니다.", menus.getMenuName());
    }

}

