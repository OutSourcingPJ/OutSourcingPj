package com.sparta.outsouringproject.store.controller;

import com.sparta.outsouringproject.store.dto.*;
import com.sparta.outsouringproject.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    /**
     * 가게 등록
     * @param  requestDto
     * @return StoreResponseDto
     */
    @PostMapping
    public ResponseEntity<StoreResponseDto> saveStore(@RequestBody CreateStoreRequestDto requestDto) {
        StoreResponseDto responseDto = storeService.save(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 가게 단건 조회
     * @param storeId
     * @return GetStoreResponseDto
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<GetStoreResponseDto> getStore(@PathVariable("storeId") Long storeId) {
        GetStoreResponseDto responseDto = storeService.getStore(storeId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 가게 전제 조회
     * @param name
     * @return GetStoreListResponseDto
     */
    @GetMapping
    public ResponseEntity<List<GetStoreListResponseDto>> getStores(@RequestParam(value = "name", required = false) String name) {
        List<GetStoreListResponseDto> storeList;
        if (name != null && !name.isEmpty()) {
            storeList = storeService.getStoresByName(name);
        } else {
            storeList = storeService.getStores();
        }
        return new ResponseEntity<>(storeList, HttpStatus.OK);
    }

    /**
     * 가게 정보 수정
     * @param requestDto
     * @return StoreResponseDto
     */
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreResponseDto> modify(@PathVariable("storeId") Long storeId, @RequestBody ModifyStoreRequestDto requestDto) {
        StoreResponseDto responseDto = storeService.modify(storeId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 가게 폐업
     * @param storeId
     * @return message
     */
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Map<String, String>> deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.delete(storeId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 삭제가 되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
