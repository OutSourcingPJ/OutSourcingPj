package com.sparta.outsouringproject.review.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class ReviewRequestDto {
    private Long id;
    private String contents;
    private List<MultipartFile> images;

}
