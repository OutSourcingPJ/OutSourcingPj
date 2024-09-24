package com.sparta.outsouringproject.store.service;

import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.common.exceptions.*;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.store.dto.*;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    @DisplayName("가게 정상 등록")
    public void saveStore_success() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);

        CreateStoreRequestDto requestDto = new CreateStoreRequestDto("bbq", 15000.0, LocalTime.parse("16:00"), LocalTime.parse("23:30"), "오늘 첫 오픈 콜라 무료");
        Store store = new Store(requestDto, user);

        given(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).willReturn(Optional.of(user));
        given(storeRepository.countActiveStoresByUser(user)).willReturn(0L);
        given(storeRepository.save(any(Store.class))).willReturn(store);

        // when
        StoreResponseDto responseDto = storeService.saveStore(requestDto, authUser);

        // then
        assertNotNull(responseDto);
        assertEquals(store.getName(), responseDto.getName());
        assertEquals(store.getOrderAmount(), responseDto.getOrderAmount());
        assertEquals(store.getOpenTime(), responseDto.getOpenTime());
        assertEquals(store.getCloseTime(), responseDto.getCloseTime());
    }

    @Test
    @DisplayName("회원을 찾을 수 없음")
    public void saveStore_userNotFound() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        CreateStoreRequestDto requestDto = new CreateStoreRequestDto("bbq", 15000.0, LocalTime.parse("16:00"), LocalTime.parse("23:30"), "오늘 첫 오픈 콜라 무료");

        given(userRepository.findByEmailAndIsDeletedFalse(user.getEmail())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
           storeService.saveStore(requestDto, authUser);
        });

        // then
        assertEquals("해당 회원을 찾을 수 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("해당 회원은 등록 권한이 없음")
    public void saveStore_userNotOner() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.USER);
        User user = User.fromAuthUser(authUser);
        CreateStoreRequestDto requestDto = new CreateStoreRequestDto("bbq", 15000.0, LocalTime.parse("16:00"), LocalTime.parse("23:30"), "오늘 첫 오픈 콜라 무료");

        given((userRepository.findByEmailAndIsDeletedFalse(user.getEmail())))
                .willReturn(Optional.of(user));
        // when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
           storeService.saveStore(requestDto, authUser);
        });

        // then
        assertEquals("해당 회원은 가게 등록 권한이 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("가게는 총 3개까지만")
    public void saveStore_StoreLimit() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        CreateStoreRequestDto requestDto = new CreateStoreRequestDto("bbq", 15000.0, LocalTime.parse("16:00"), LocalTime.parse("23:30"), "오늘 첫 오픈 콜라 무료");

        given(userRepository.findByEmailAndIsDeletedFalse(user.getEmail()))
                .willReturn(Optional.of(user));
        given(storeRepository.countActiveStoresByUser(user)).willReturn(3L);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
           storeService.saveStore(requestDto, authUser);
        });

        // then
        assertEquals("가게는 총 3개만 만들 수 있습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("가게 단건 정상 조회")
    public void getStore_success() {
        // given
        Long storeId = 1L;
        Store store = new Store();
        store.setStoreId(storeId);
        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        GetStoreResponseDto responseDto = storeService.getStore(storeId);

        // then
        assertNotNull(responseDto);
    }

    @Test
    @DisplayName("가게 단건 조회 실패")
    public void getStore_notFound() {
        // given
        Long storeId = 1L;

        given(storeRepository.checkStore(storeId)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
           storeService.getStore(storeId);
        });

        // then
        assertEquals("해당 가게가 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("가게 다건 조회 성공")
    public void getStores_success() {
        // given
        List<Store> stores = List.of(new Store(), new Store());
        given(storeRepository.findAllActiveStores()).willReturn(stores);

        // when
        GetStoreListResponseDto responseDto = storeService.getStores();

        // then
        assertNotNull(responseDto);
        assertEquals(2, responseDto.getStoreList().size());
    }

    @Test
    @DisplayName("해당 가게가 존재하지 않음")
    public void getStores_notFound() {
        // given
        given(storeRepository.findAllActiveStores()).willReturn(Collections.emptyList());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.getStores();
        });

        // then
        assertEquals("등록된 가게가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("특정 가게 조회 성공")
    public void getStoresByName_success() {
        // given
        String name = "bbq";
        List<Store> stores = List.of(new Store(), new Store());

        given(storeRepository.findStoresByName(name)).willReturn(stores);

        // when
        GetStoreListResponseDto responseDto = storeService.getStoresByName(name);

        // then
        assertNotNull(responseDto);
        assertEquals(2, responseDto.getStoreList().size());
    }

    @Test
    @DisplayName("특정 기게 조회 실패")
    public void getStoresByName_notFound() {
        // given
        String name = "bbq";

        given(storeRepository.findStoresByName(name)).willReturn(Collections.emptyList());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.getStoresByName(name);
        });

        // then
        assertEquals("해당 이름을 가진 가게가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("가게 수정 성공")
    public void modifyStore_success() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setUser(user);

        ModifyStoreRequestDto requestDto = new ModifyStoreRequestDto
                ("bhc", 12000.0, LocalTime.parse("16:00"), LocalTime.parse("04:30"), "리뷰 이벤트 실행");

        given(storeRepository.checkStore(store.getStoreId())).willReturn(Optional.of(store));
        given(storeRepository.save(any(Store.class))).willReturn(store);

        // when
        StoreResponseDto responseDto = storeService.modify(store.getStoreId(), requestDto, authUser);

        // then
        assertNotNull(responseDto);
        assertEquals("bhc", responseDto.getName());
        assertEquals(12000.0, responseDto.getOrderAmount());
        assertEquals(LocalTime.parse("16:00"), responseDto.getOpenTime());
        assertEquals(LocalTime.parse("04:30"), requestDto.getCloseTime());
        assertEquals("리뷰 이벤트 실행", responseDto.getNotice());
    }

    @Test
    @DisplayName("수정하려는 가게가 존재하지 않음")
    public void modifyStore_notFound() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setUser(user);

        ModifyStoreRequestDto requestDto = new ModifyStoreRequestDto
                ("bhc", 12000.0, LocalTime.parse("16:00"), LocalTime.parse("04:30"), "리뷰 이벤트 실행");

        given(storeRepository.checkStore(store.getStoreId())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.modify(store.getStoreId(), requestDto, authUser);
        });

        // then
        assertEquals("해당 가게가 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("수정 권한이 없습니다.")
    public void modifyStore_accessDenied() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setUser(user);

        ModifyStoreRequestDto requestDto = new ModifyStoreRequestDto
                ("bhc", 12000.0, LocalTime.parse("16:00"), LocalTime.parse("04:30"), "리뷰 이벤트 실행");

        given(storeRepository.checkStore(store.getStoreId())).willReturn(Optional.of(store));

        AuthUser newUser = new AuthUser(2L, "test2@example.com", Role.OWNER);

        // when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            storeService.modify(store.getStoreId(), requestDto, newUser);
        });

        // then
        assertEquals("수정 권한이 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("가계 폐업 성공")
    public void deleteStore_success() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        storeService.delete(storeId, authUser);

        // then
        verify(storeRepository, times(1)).checkStore(storeId);
    }

    @Test
    @DisplayName("폐업 하려는 가게가 존재하지 않음")
    public void deleteStore_notFound() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.delete(store.getStoreId(), authUser);
        });

        // then
        assertEquals("해당 가게가 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("폐업 권한이 없음")
    public void deleteStore_accessDenied() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);

        given(storeRepository.checkStore(store.getStoreId())).willReturn(Optional.of(store));

        AuthUser newUser = new AuthUser(2L, "test2@example.com", Role.OWNER);

        // when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            storeService.delete(store.getStoreId(), newUser);
        });

        // then
        assertEquals("폐업 권한이 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("가게 광고 선정 성공")
    public void checkAdvertise_success() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);
        store.setAdvertise(false);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        storeService.checkAdvertise(storeId, authUser);

        // then
        assertTrue(store.getAdvertise());
    }

    @Test
    @DisplayName("광고 선정하려는 가게가 없음")
    public void checkAdvertise_notFoundStore() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.checkAdvertise(storeId, authUser);
        });

        // then
        assertEquals("해당 가게가 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("광고를 신청하려는 가게의 주인이 아님")
    public void checkAdvertise_notOwner() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);
        store.setAdvertise(false);

        AuthUser newUser = new AuthUser(2L, "test2@example.com", Role.OWNER);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
           storeService.checkAdvertise(storeId, newUser);
        });

        // then
        assertEquals("해당 가게 주인이 아닙니다!", exception.getMessage());
    }

    @Test
    @DisplayName("이미 광고로 선정된 가게를 또 광고 신청함")
    public void checkAdvertise_alreadyAdvertised() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);
        store.setAdvertise(true);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.checkAdvertise(storeId, authUser);
        });

        // then
        assertEquals("해당 가게는 이미 광고로 지정되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("가게 광고 해제 성공")
    public void unCheckAdvertise_success() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);
        store.setAdvertise(true);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        storeService.unCheckAdvertise(storeId, authUser);

        // then
        assertFalse(store.getAdvertise());
    }

    @Test
    @DisplayName("광고 해제하려는 가게가 없음")
    public void unCheckAdvertise_notFoundStore() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.unCheckAdvertise(storeId, authUser);
        });

        // then
        assertEquals("해당 가게가 없습니다!", exception.getMessage());
    }

    @Test
    @DisplayName("광고 해제신청할 수 있는 권한 없음")
    public void unCheckAdvertise_notOwner() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);
        store.setAdvertise(false);

        AuthUser newUser = new AuthUser(2L, "test2@example.com", Role.OWNER);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            storeService.unCheckAdvertise(storeId, newUser);
        });

        // then
        assertEquals("해당 가게 주인이 아닙니다!", exception.getMessage());
    }

    @Test
    @DisplayName("이미 광고에서 해제된 가게를 다시 해제 신청함")
    public void unCheckAdvertise_alreadyUnAdvertised() {
        // given
        long storeId = 1;
        AuthUser authUser = new AuthUser(1L, "test@example.com", Role.OWNER);
        User user = User.fromAuthUser(authUser);
        Store store = new Store();
        store.setStoreId(storeId);
        store.setUser(user);
        store.setAdvertise(false);

        given(storeRepository.checkStore(storeId)).willReturn(Optional.of(store));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            storeService.unCheckAdvertise(storeId, authUser);
        });

        // then
        assertEquals("해당 가게는 광고 선정 상태가 아닙니다.", exception.getMessage());
    }
}
