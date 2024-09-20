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
        log.info("::: 가게 저장 로직 동작 :::");
        Long userId = 1L;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다!"));

        // 회원파트 생성되면 적용 + delte값 설정되어있을테니 findById에서 JPQL로 수정
//        if(user.getRole() != RoleType.OWNER) {
//            throw new IllegalArgumentException("해당 회원은 가게 등록 권한이 없습니다!");
//        }
        log.info("::: 가게 갯수 확인 :::");
        Long storeCount = storeRepository.countActiveStoresByUser(user);
        if(storeCount >= 3) {
            throw new IllegalArgumentException("가게는 총 3개만 만들 수 있습니다!");
        }

        Store store = new Store(requestDto, user);
        log.info("::: 가게 저장 로직 :::");
        Store saveStore = storeRepository.save(store);
        log.info("::: 가게 저장 완료 :::");
        return new StoreResponseDto(saveStore);
    }

    // 가게 단건 조회
    @Transactional(readOnly = true)
    public GetStoreResponseDto getStore(Long storeId) {
        log.info("::: 가게 단건 조회 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다!"));
        return new GetStoreResponseDto(store);
    }

    // 가게 전체 조회
    @Transactional(readOnly = true)
    public GetStoreListResponseDto getStores() {
        log.info("::: 가게 전체 조회 :::");
        List<Store> stores = storeRepository.findAllActiveStores();
        if (stores.isEmpty()) {
            throw new IllegalArgumentException("등록된 가게가 존재하지 않습니다.");
        }
        List<StoreResponseDto> storeDtoList = stores.stream()
                .map(StoreResponseDto::new)
                .collect(Collectors.toList());

        return new GetStoreListResponseDto(storeDtoList);
    }

    // 특정 가게 이름 전체 조회
    @Transactional(readOnly = true)
    public GetStoreListResponseDto getStoresByName(String name) {
        log.info("::: 특정 가게 조회 :::");
        List<Store> stores = storeRepository.findStoresByName(name);
        if (stores.isEmpty()) {
            throw new IllegalArgumentException("해당 이름을 가진 가게가 존재하지 않습니다.");
        }
        List<StoreResponseDto> storeDtoList = stores.stream()
                .map(StoreResponseDto::new)
                .collect(Collectors.toList());

        return new GetStoreListResponseDto(storeDtoList);
    }

    // 가게 수정
    @Transactional
    public StoreResponseDto modify(Long storeId, ModifyStoreRequestDto requestDto) {
        log.info("::: 가게 수정 로직 동작 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다!"));
        // 권한 체크 코드
//        if(!store.getUser().getUserId().equals(ArgumentResolver에서 반환된 userId값)) {
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
        // 광고 설정 advertise값을 string으로 true, flase 입력받음, 입력 값에 따라서 세팅 변경
        if(requestDto.getAdvertise().equals("true")) {
            store.setAdvertise(true);
        }
        if(requestDto.getAdvertise().equals("false")) {
            store.setAdvertise(false);
        }
        Store newStore = storeRepository.save(store);
        log.info("::: 가게 수정 완료 :::");
        return new StoreResponseDto(newStore);
    }

    // 가게 폐업
    @Transactional
    public void delete(Long storeId) {
        log.info("::: 가게 폐업 로직 동작 :::");
        Store store = storeRepository.checkStore(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게가 없습니다!"));
        // 권한 체크 코드
//        if(!store.getUser().getUserId().equals(ArgumentResolver에서 반환된 userId값)) {
//            throw new IllegalArgumentException("폐업 권한이 없습니다!");
//        }
        store.setStoreStatus(true);
        log.info("::: 가게 폐업 완 :::");
    }
}
