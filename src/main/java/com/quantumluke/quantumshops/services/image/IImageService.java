package com.quantumluke.quantumshops.services.image;


import com.quantumluke.quantumshops.dto.ImageDto;
import com.quantumluke.quantumshops.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);
    Image updateImage(MultipartFile file, Long imageId);
}
