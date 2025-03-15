package com.kasirpinter.pos.controller;

import java.util.Optional;

import com.kasirpinter.pos.entity.FileManager;
import com.kasirpinter.pos.enums.FileEntity;
import com.kasirpinter.pos.repository.FileManagerRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.kasirpinter.pos.entity.Product;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.util.TreeGetEntity;
import com.kasirpinter.pos.util.UploadStreamHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Tag(name = "Byte Downloader")
@Slf4j
public class ByteDownloaderController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FileManagerRepository fileManagerRepository;

    // get stream file user avatar
    @Operation(summary = "Show Image User", description = "Show Image User")
    @GetMapping("/get/file/user/{id}/avatar")
    public ResponseEntity<byte[]> downloadFileAvatar(@PathVariable String id) {
        try {
            Users user = TreeGetEntity.parsingUserByProjection(id, userRepository);
            FileManager fileManager = fileManagerRepository.findByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, user.getId());
            HttpHeaders headers = UploadStreamHelper.HeaderStreamHelper(fileManager.getFileName());
            return new ResponseEntity<>(fileManager.getFile(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // get stream file user cover
    @Operation(summary = "Show Image User", description = "Show Image User")
    @GetMapping("/get/file/user/{id}/cover")
    public ResponseEntity<byte[]> downloadFileCover(@PathVariable String id) {
        try {
            Users user = TreeGetEntity.parsingUserByProjection(id, userRepository);
            FileManager fileManager = fileManagerRepository.findByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, user.getId());
            HttpHeaders headers = UploadStreamHelper.HeaderStreamHelper(fileManager.getFileName());
            return new ResponseEntity<>(fileManager.getFile(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    // get stream file
    @Operation(summary = "read product image", description = "API for reading product image")
    @GetMapping("/get/file/product/{fileId}")
    public ResponseEntity<byte[]> readThumbnailGallery(@PathVariable("fileId") String fileId) {
        Optional<Product> optionalData = productRepository.findBySecureId(fileId);
        if (optionalData.isPresent()) {
            Product data = optionalData.get();
            HttpHeaders headers = setHeaderFile(data.getImageName());
            return new ResponseEntity<>(data.getImage(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // set header
    private HttpHeaders setHeaderFile(String name) {
        // Membuat response header untuk unduhan
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(name.contains(".svg") ? MediaType.parseMediaType("image/svg+xml") : MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(name)
                .build());
        return headers;
    }

}
