package com.ddong_kka.board_api.image.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class ImageUploadDto {

    private List<MultipartFile> files;
}
