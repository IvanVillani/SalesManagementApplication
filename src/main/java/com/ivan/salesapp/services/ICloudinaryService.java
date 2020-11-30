package com.ivan.salesapp.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadImage(MultipartFile multipartFile) throws IOException;
}
