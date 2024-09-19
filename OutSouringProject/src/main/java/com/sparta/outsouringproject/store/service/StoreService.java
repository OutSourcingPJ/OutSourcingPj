package com.sparta.outsouringproject.store.service;

import com.sparta.outsouringproject.store.dto.*;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
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
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 가게 저장
    @Transactional
    public StoreResponseDto save(CreateStoreRequestDto requestDto) {
        Long userId = 1L;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다!"));

        // 회원파트 생성되면 적용
//        if(user.getRole() != RoleType.OWNER) {
//            throw new IllegalArgumentException("해당 회원은 가게 등록 권한이 없습니다!");
//        }
//        if(user.getStores().size() >= 3) {
//            throw new IllegalArgumentException("가게는 총 3개만 만들 수 있습니다!");
//        }
        Store store = new Store(requestDto, user);
        Store saveStore = storeRepository.save(store);
        return new StoreResponseDto(saveStore);
    }

    // 가게 단건 조회
    @Transactional(readOnly = true)
    public GetStoreResponseDto getStore(Long storeId) {
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다!"));
        return new GetStoreResponseDto(store);
    }

    // 가게 전체 조회
    @Transactional(readOnly = true)
    public List<GetStoreListResponseDto> getStores() {
        List<Store> stores = storeRepository.findAllActiveStores();
        if (stores.isEmpty()) {
            throw new IllegalArgumentException("등록된 가게가 존재하지 않습니다.");
        }
        return stores.stream()
                .map(GetStoreListResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 가게 이름 전체 조회
    @Transactional(readOnly = true)
    public List<GetStoreListResponseDto> getStoresByName(String name) {
        List<Store> stores = storeRepository.findStoresByName(name);
        if (stores.isEmpty()) {
            throw new IllegalArgumentException("해당 이름을 가진 가게가 존재하지 않습니다.");
        }
        return stores.stream()
                .map(GetStoreListResponseDto::new)
                .collect(Collectors.toList());
    }

    // 가게 수정
    @Transactional
    public StoreResponseDto modify(Long storeId, ModifyStoreRequestDto requestDto) {
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다!"));
        // 권한 체크 코드
//        if(!store.getUser().getUserId().equals(userId)) {
//            throw new IllegalArgumentException("수정 권한이 없습니다!");
//        }

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
        Store newStore = storeRepository.save(store);
        return new StoreResponseDto(newStore);
    }

    // 가게 폐업
    @Transactional
    public void delete(Long storeId) {
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다!"));
        // 권한 체크 코드
//        if(!store.getUser().getUserId().equals(userId)) {
//            throw new IllegalArgumentException("수정 권한이 없습니다!");
//        }

        store.setStoreStatus(true);
    }
}
