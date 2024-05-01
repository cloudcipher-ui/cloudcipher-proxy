package com.cloudcipher.cloudcipher_proxy.ReEncryption.service;

import com.cloudcipher.cloudcipher_proxy.ReEncryption.exception.InvalidSecretKeyException;
import com.cloudcipher.cloudcipher_proxy.ReEncryption.util.CloudCipher;

import com.cloudcipher.cloudcipher_proxy.ReEncryption.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.cloudcipher.cloudcipher_proxy.ReEncryption.util.Utility.*;


@Service
public class ReEncryptionService {

    @Value("${secret-key}")
    private String secretKey;

    private final CloudCipher cc;

    public ReEncryptionService() throws Exception {
        cc = new CloudCipher();
    }

    public byte[] reEncrypt(MultipartFile file, MultipartFile iv, MultipartFile rg, String key) throws Exception {
        if (!key.equals(secretKey)) {
            throw new InvalidSecretKeyException("Invalid secret key.");
        }

        byte[] fileBytes = file.getBytes();
        byte[] ivBytes = iv.getBytes();

        cc.setIV(ivBytes);
        long[][] c = byteToLongs(fileBytes);

        int[][] rgData = Utility.byteArrayToIntegerArray(rg.getBytes(), 16);

        long[][] reencrypted = cc.reEncrypt(rgData[0], rgData[1], rgData[2], rgData[3], c);
        return longsToByte(reencrypted);
    }
}
