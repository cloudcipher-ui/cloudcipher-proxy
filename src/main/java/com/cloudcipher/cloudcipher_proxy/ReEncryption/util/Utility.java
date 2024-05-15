package com.cloudcipher.cloudcipher_proxy.ReEncryption.util;

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

    public static byte[] longsToByte(long[][] arr) {
        int numBlocks = arr.length;
        byte[] fileBytes = new byte[numBlocks * BLOCK_SIZE];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < 2; j++) {
                byte[] temp = {
                    (byte) ((arr[i][j] >> 56) & 0xFF),
                    (byte) ((arr[i][j] >> 48) & 0xFF),
                    (byte) ((arr[i][j] >> 40) & 0xFF),
                    (byte) ((arr[i][j] >> 32) & 0xFF),
                    (byte) ((arr[i][j] >> 24) & 0xFF),
                    (byte) ((arr[i][j] >> 16) & 0xFF),
                    (byte) ((arr[i][j] >> 8) & 0xFF),
                    (byte) (arr[i][j] & 0xFF)
                };
                System.arraycopy(temp, 0, fileBytes, i * BLOCK_SIZE + j * 8, 8);
            }
        }
        return fileBytes;
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

    public static int[][] parseRG(String rg) {
        String[] rgHexs = rg.split(" ");

        int[][] rgData = new int[rgHexs.length][];
        for (int i = 0; i < rgHexs.length; i++) {
            byte[] temp = hexToBytes(rgHexs[i]);
            rgData[i] = new int[temp.length];
            for (int j = 0; j < temp.length; j++) {
                rgData[i][j] = temp[j] & 0xFF;
            }
        }
        return rgData;
    }
}
