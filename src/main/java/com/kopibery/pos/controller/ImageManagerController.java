package com.kopibery.pos.controller;

import com.kopibery.pos.entity.Product;
import com.kopibery.pos.entity.Users;
import com.kopibery.pos.repository.ProductRepository;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.util.TreeGetEntity;
import com.kopibery.pos.util.UploadStreamHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "Image API")
@Slf4j
public class ImageManagerController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // get stream file user
    @Operation(summary = "Show Image User", description = "Show Image User")
    @GetMapping("/api/v1/image/user/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        try {
            Users user = TreeGetEntity.parsingUserByProjection(id, userRepository);
            HttpHeaders headers = UploadStreamHelper.HeaderStreamHelper(user.getAvatarName());

            return new ResponseEntity<>(user.getAvatar(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // get stream file product
    @Operation(summary = "Show Image Product", description = "Show Image Product")
    @GetMapping("/api/v1/image/product/{id}")
    public ResponseEntity<byte[]> showImageProduct(@PathVariable String id) {
        try {
            Product data = TreeGetEntity.parsingProductByProjection(id, productRepository);
            HttpHeaders headers = UploadStreamHelper.HeaderStreamHelper(data.getImageName());

            return new ResponseEntity<>(data.getImage(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
