package com.cloudcipher.cloudcipher_proxy.ReEncryption.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utility {

    private static final int BLOCK_SIZE = 16;

    public static long[][] byteToLongs(byte[] fileBytes) {
        int numBlocks = fileBytes.length / BLOCK_SIZE;
        long[][] longs = new long[numBlocks][2];
        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 8; k++) {
                    longs[i][j] <<= 8;
                    longs[i][j] |= (fileBytes[i * BLOCK_SIZE + j * 8 + k] & 0xFF);
                }
            }
        }
        return longs;
    }

    public static byte[] longsToByte(long[][] longs) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            for (long[] aLong : longs) {
                for (int j = 0; j < 2; j++) {
                    byte[] temp = {
                            (byte) ((aLong[j] >> 56) & 0xFF),
                            (byte) ((aLong[j] >> 48) & 0xFF),
                            (byte) ((aLong[j] >> 40) & 0xFF),
                            (byte) ((aLong[j] >> 32) & 0xFF),
                            (byte) ((aLong[j] >> 24) & 0xFF),
                            (byte) ((aLong[j] >> 16) & 0xFF),
                            (byte) ((aLong[j] >> 8) & 0xFF),
                            (byte) ((aLong[j]) & 0xFF)
                    };
                    byteArrayOutputStream.write(temp);
                }
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] hexToBytes(String hexString) {
        int arrLength = hexString.length() / 2;
        byte[] byteArray = new byte[arrLength];

        for (int i = 0; i < arrLength; i++) {
            int idx = i * 2;
            int v = Integer.parseInt(hexString.substring(idx, idx + 2), 16);
            byteArray[i] = (byte) v;
        }
        return byteArray;
    }

    public static int[][] byteArrayToIntegerArray(byte[] bytes, int blockSize) {
        int numOfBlocks = bytes.length / blockSize;
        int[][] array = new int[numOfBlocks][blockSize];
        for (int i = 0; i < numOfBlocks; i++) {
            byte[] temp = new byte[blockSize];
            System.arraycopy(bytes, i * blockSize, temp, 0, blockSize);
            for (int j = 0; j < blockSize; j++) {
                // Convert the byte to integer, considering it as unsigned
                array[i][j] = temp[j] & 0xFF;
            }
        }
        return array;
    }
}
