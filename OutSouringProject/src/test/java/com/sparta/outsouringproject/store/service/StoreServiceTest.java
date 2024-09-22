package com.sparta.outsouringproject.store.service;

import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.store.dto.CreateStoreRequestDto;
import com.sparta.outsouringproject.store.dto.StoreResponseDto;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import com.sparta.outsouringproject.user.entity.Role;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

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
}
