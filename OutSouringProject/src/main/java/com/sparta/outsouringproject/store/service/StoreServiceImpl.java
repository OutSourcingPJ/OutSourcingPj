package com.sparta.outsouringproject.store.service;


import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.exceptions.AccessDeniedException;
import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.store.dto.*;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService{
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 가게 저장
    @Transactional
    @Override
    public StoreResponseDto saveStore(CreateStoreRequestDto requestDto, AuthUser authUser) {
        log.info("::: 가게 저장 로직 동작 :::");

        User user = userRepository.findByEmailAndIsDeletedFalse(authUser.getEmail())
                .orElseThrow(() -> new InvalidRequestException("해당 회원을 찾을 수 없습니다!"));

        log.info("::: 회원 검사 로직 동작 :::");
        if(user.getRole() != Role.OWNER) {
            throw new AccessDeniedException("해당 회원은 가게 등록 권한이 없습니다!");
        }
        log.info("::: 가게 갯수 확인 :::");
        Long storeCount = storeRepository.countActiveStoresByUser(user);
        if(storeCount >= 3) {
            throw new InvalidRequestException("가게는 총 3개만 만들 수 있습니다!");
        }

        Store store = new Store(requestDto, user);
        log.info("::: 가게 저장 로직 :::");
        Store saveStore = storeRepository.save(store);
        log.info("::: 가게 저장 완료 :::");
        return new StoreResponseDto(saveStore);
    }

    // 가게 단건 조회
    @Transactional(readOnly = true)
    @Override
    public GetStoreResponseDto getStore(Long storeId) {
        log.info("::: 가게 단건 조회 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new InvalidRequestException("해당 가게가 없습니다!"));
        return new GetStoreResponseDto(store);
    }

    // 가게 전체 조회
    @Transactional(readOnly = true)
    @Override
    public GetStoreListResponseDto getStores() {
        log.info("::: 가게 전체 조회 :::");
        List<Store> stores = storeRepository.findAllActiveStores();
        if (stores.isEmpty()) {
            throw new InvalidRequestException("등록된 가게가 존재하지 않습니다.");
        }
        List<StoreResponseDto> storeDtoList = stores.stream()
                .map(StoreResponseDto::new)
                .collect(Collectors.toList());

        return new GetStoreListResponseDto(storeDtoList);
    }

    // 특정 가게 이름 전체 조회
    @Transactional(readOnly = true)
    @Override
    public GetStoreListResponseDto getStoresByName(String name) {
        log.info("::: 특정 가게 조회 :::");
        List<Store> stores = storeRepository.findStoresByName(name);
        if (stores.isEmpty()) {
            throw new InvalidRequestException("해당 이름을 가진 가게가 존재하지 않습니다.");
        }
        List<StoreResponseDto> storeDtoList = stores.stream()
                .map(StoreResponseDto::new)
                .collect(Collectors.toList());

        return new GetStoreListResponseDto(storeDtoList);
    }

    // 가게 수정
    @Transactional
    @Override
    public StoreResponseDto modify(Long storeId, ModifyStoreRequestDto requestDto, AuthUser authUser) {
        log.info("::: 가게 수정 로직 동작 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new InvalidRequestException("해당 가게가 없습니다!"));
        // 권한 체크 코드
        if(!store.getUser().getId().equals(authUser.getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다!");
        }
        if(requestDto.getName() != null) {
            store.changeName(requestDto.getName());
        }
        if(requestDto.getOrderAmount() != null) {
            store.changeAmount(requestDto.getOrderAmount());
        }
        if(requestDto.getOpenTime() != null) {
            store.changeOpenTime(requestDto.getOpenTime());
        }
        if(requestDto.getCloseTime() != null) {
            store.changeCloseTime(requestDto.getCloseTime());
        }
        if(requestDto.getNotice() != null) {
            store.changeNotice(requestDto.getNotice());
        }
        Store newStore = storeRepository.save(store);
        log.info("::: 가게 수정 완료 :::");
        return new StoreResponseDto(newStore);
    }

    // 가게 폐업
    @Transactional
    @Override
    public void delete(Long storeId, AuthUser authUser) {
        log.info("::: 가게 폐업 로직 동작 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new InvalidRequestException("해당 가게가 없습니다!"));
        // 권한 체크 코드
        if(!store.getUser().getId().equals(authUser.getId())) {
            throw new AccessDeniedException("폐업 권한이 없습니다!");
        }
        store.getMenuList().forEach(Menu::deleteMenu);
        store.deleteStore();
        log.info("::: 가게 폐업 완 :::");
    }

    @Transactional
    @Override
    public void checkAdvertise(Long storeId, AuthUser authUser) {
        log.info("::: 가게 광고 선정 로직 동작 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new InvalidRequestException("해당 가게가 없습니다!"));
        // 권한 체크 코드
        if(!store.getUser().getId().equals(authUser.getId())) {
            throw new AccessDeniedException("해당 가게 주인이 아닙니다!");
        }
        if(store.getAdvertise().equals(true)) {
            throw new InvalidRequestException("해당 가게는 이미 광고로 지정되었습니다.");
        }
        store.checkAdvertise();
        log.info("::: 가게 광고 선정 로직 완 :::");
    }

    @Transactional
    @Override
    public void unCheckAdvertise(Long storeId ,AuthUser authUser) {
        log.info("::: 가게 광고 해제 로직 동작 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new InvalidRequestException("해당 가게가 없습니다!"));
        // 권한 체크 코드
        if(!store.getUser().getId().equals(authUser.getId())) {
            throw new AccessDeniedException("해당 가게 주인이 아닙니다!");
        }
        if(store.getAdvertise().equals(false)) {
            throw new InvalidRequestException("해당 가게는 광고 선정 상태가 아닙니다.");
        }
        store.unCheckAdvertise();
        log.info("::: 가게 광고 해제 로직 완 :::");
    }
}
