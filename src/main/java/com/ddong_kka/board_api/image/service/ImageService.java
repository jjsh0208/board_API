package com.ddong_kka.board_api.image.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${upload.directory}")
    private String uploadDirectory;

    public String saveFile(MultipartFile file) throws IOException{

        // 디렉터리 생성
        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()){
            uploadDir.mkdirs();
        }


        // 파일 저장 이름 생성
        String originalFileName = file.getOriginalFilename();
        String saveName = UUID.randomUUID() + "_" + originalFileName;
        Path savePath = Paths.get(uploadDirectory + saveName);


        // 파일저장
        Files.copy(file.getInputStream(),savePath);

        return savePath.toString();
    }

}
