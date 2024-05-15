package com.cloudcipher.cloudcipher_proxy.ReEncryption.controller;

import com.cloudcipher.cloudcipher_proxy.ReEncryption.service.ReEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class ReEncryptionController {

    @Autowired
    private ReEncryptionService reEncryptionService;

    @PostMapping("/")
    public @ResponseBody byte[] reEncrypt(@RequestParam MultipartFile file, @RequestParam MultipartFile iv, @RequestParam String rg, @RequestParam String key) {
        try {
            return reEncryptionService.reEncrypt(file, iv, rg, key);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
