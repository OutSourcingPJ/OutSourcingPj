package com.sparta.outsouringproject.store.controller;

import com.sparta.outsouringproject.common.dto.ResponseDto;
import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.service.MenuService;
import com.sparta.outsouringproject.store.dto.*;
import com.sparta.outsouringproject.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private MenuService menuService;

    /**
     * 가게 등록
     * @param  requestDto
     * @return StoreResponseDto
     */
    @PostMapping
    public ResponseDto<StoreResponseDto> saveStore(@RequestBody CreateStoreRequestDto requestDto) {
        StoreResponseDto responseDto = storeService.saveStore(requestDto);
        return ResponseDto.of(200, "가게가 추가되었습니다.", responseDto);
    }

    /**
     * 가게 단건 조회
     * @param storeId
     * @return GetStoreResponseDto
     */
    @GetMapping("/{storeId}")
    public ResponseDto<GetStoreResponseDto> getStore(@PathVariable("storeId") Long storeId) {
        GetStoreResponseDto responseDto = storeService.getStore(storeId);
        return ResponseDto.of(200, "성공적으로 조회되었습니다.", responseDto);
    }

    /**
     * 가게 전제 조회
     * @param name
     * @return GetStoreListResponseDto
     */
    @GetMapping
    public ResponseDto<GetStoreListResponseDto> getStores(@RequestParam(value = "name", required = false) String name) {
        GetStoreListResponseDto storeList;
        if (name != null && !name.isEmpty()) {
            storeList = storeService.getStoresByName(name);
        } else {
            storeList = storeService.getStores();
        }
        return ResponseDto.of(200, "성공적으로 조회되었습니다.", storeList);
    }

    /**
     * 가게 정보 수정
     * @param requestDto
     * @return StoreResponseDto
     */
    @PatchMapping("/{storeId}")
    public ResponseDto<StoreResponseDto> modify(@PathVariable("storeId") Long storeId, @RequestBody ModifyStoreRequestDto requestDto) {
        StoreResponseDto responseDto = storeService.modify(storeId, requestDto);
        return ResponseDto.of(200, "가게 정보가 수정되었습니다.", responseDto);
    }

    /**
     * 가게 폐업
     * @param storeId
     * @return message
     */
    @DeleteMapping("/{storeId}")
    public ResponseDto<String> deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.delete(storeId);
        return ResponseDto.of(200, "성공적으로 삭제되었습니다.");
    }

    // 메뉴 리스트 조회 (가게 ID로 메뉴 리스트 가져오기)
    @GetMapping("/{storeId}/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenuListByStore(@PathVariable("storeId") Long storeId) {
        List<MenuResponseDto> menuList = menuService.getMenuList(storeId);  // 메뉴 서비스 호출
        return new ResponseEntity<>(menuList, HttpStatus.OK);
    }

}
