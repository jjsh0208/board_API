package com.ddong_kka.board_api.image.repository;

import com.ddong_kka.board_api.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {
}
