package com.sparta.outsouringproject.review.dto.request;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
public class ReviewRequestDto {
    private String contents;
    private List<MultipartFile> images;

}
