package com.sparta.outsouringproject.store.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
        User user = new User();
        CreateStoreRequestDto requestDto = new CreateStoreRequestDto("bbq", 15000.0, LocalTime.parse("16:00"), LocalTime.parse("23:30"));
        Store store = new Store(requestDto, user);

        given(storeRepository.save(any(Store.class))).willReturn(store);

        // when
        StoreResponseDto responseDto = storeService.saveStore(requestDto, user.getEmail());

        // then
        assertNotNull(responseDto);
        assertEquals(store.getName(), responseDto.getName());
        assertEquals(store.getOrderAmount(), responseDto.getOrderAmount());
        assertEquals(store.getOpenTime(), responseDto.getOpenTime());
        assertEquals(store.getCloseTime(), responseDto.getCloseTime());
    }
}
