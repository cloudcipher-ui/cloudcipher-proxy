package com.cloudcipher.cloudcipher_proxy.ReEncryption.util;

/**
 * @author https://www.linkedin.com/in/rizky-putri-meiliasari/
 */
public class CloudCipher {
    AONTH aonth;
    Permutation perm;
    KeyGenerator g;
    int[] iv;

    public CloudCipher() throws Exception {
        aonth = new AONTH();
        perm = new Permutation();
        g = new KeyGenerator();
        iv = new int[16];
    }

    public void setIV(byte[] iv) {
        for (int i = 0; i < 16; i++) {
            this.iv[i] = iv[i] & 0xFF;
        }
    }

    public long[][] reEncrypt(int[] ck1, int[] k2, int[] k2Random, int[] ck3, long[][] c) throws Exception {
        // P2 = PG (l, K2), P2' = PG(l, K2')
        byte[] tempKey = new byte[16];
        byte[] tempKeyRandom = new byte[16];
        for (int i = 0; i < 16; i++) {
            tempKey[i] = (byte) (k2[i] & 0xFF);
            tempKeyRandom[i] = (byte) (k2Random[i] & 0xFF);
        }
        int[] p2 = perm.keyGenerator(16, tempKey);
        int[] p2Random = perm.keyGenerator(16, tempKeyRandom);

        int[][] cBytes = new int[c.length][16];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < 2; j++) {
                int[] temp = new int[]{(int) ((c[i][j] >> 56) & 0xFF), (int) ((c[i][j] >> 48) & 0xFF),
                        (int) ((c[i][j] >> 40) & 0xFF), (int) ((c[i][j] >> 32) & 0xFF), (int) ((c[i][j] >> 24) & 0xFF),
                        (int) ((c[i][j] >> 16) & 0xFF), (int) ((c[i][j] >> 8) & 0xFF), (int) (c[i][j] & 0xFF)};
                System.arraycopy(temp, 0, cBytes[i], j * 8, temp.length);
            }
        }
        int[][] cPermutedBytes = new int[c.length - 1][16];
        int[] temp1 = new int[16];
        int[] temp2 = new int[16];
        // c'[i] = PE (c[i] xor PE (c[i − 1][1...l], P2), CK1)
        for (int i = cBytes.length - 1; i > 0; i--) {
            temp1 = perm.permutation(cBytes[i - 1], p2);
            for (int j = 0; j < 16; j++) {
                temp2[j] = cBytes[i][j] ^ temp1[j];
            }
            cPermutedBytes[i - 1] = perm.permutation(temp2, ck1);
        }
        // c''[1]...c''[n] = PE (c'[1]...c'[n], CK3)
        long[][] cPermuted = new long[cPermutedBytes.length][2];
        for (int i = 0; i < cPermutedBytes.length; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 8; k++) {
                    cPermuted[i][j] <<= 8;
                    cPermuted[i][j] |= (cPermutedBytes[i][j * 8 + k] & 0xFF);
                }
            }
        }
        long[][] cPermuted2 = perm.permutationArray(cPermuted, ck3);
        // c''[0] = (PE (c[0] xor PE (iv[1...l], P2), CK1)) xor (PE(iv[1...l], P2'))
        int[] ivPermuted = perm.permutation(iv, p2);
        int[] ivRandomPermuted = perm.permutation(iv, p2Random);
        int[] temp = new int[16];
        for (int i = 0; i < 16; i++) {
            temp[i] = ivPermuted[i] ^ cBytes[0][i];
        }
        temp = perm.permutation(temp, ck1);
        for (int i = 0; i < 16; i++) {
            temp[i] = temp[i] ^ ivRandomPermuted[i];
        }
        int[][] cAppend = new int[c.length][16];
        cAppend[0] = temp;

        // c[i] = c''[i] xor PE(c''[i − 1][1...l], P2')
        for (int i = 0; i < c.length - 1; i++) {
            for (int j = 0; j < 2; j++) {
                int[] a = new int[]{(int) ((cPermuted2[i][j] >> 56) & 0xFF), (int) ((cPermuted2[i][j] >> 48) & 0xFF),
                        (int) ((cPermuted2[i][j] >> 40) & 0xFF), (int) ((cPermuted2[i][j] >> 32) & 0xFF),
                        (int) ((cPermuted2[i][j] >> 24) & 0xFF), (int) ((cPermuted2[i][j] >> 16) & 0xFF),
                        (int) ((cPermuted2[i][j] >> 8) & 0xFF), (int) (cPermuted2[i][j] & 0xFF)};
                System.arraycopy(a, 0, cAppend[i + 1], j * 8, a.length);
            }
        }

        for (int i = 0; i < c.length - 1; i++) {
            temp = perm.permutation(cAppend[i], p2Random);
            for (int j = 0; j < 16; j++) {
                cAppend[i + 1][j] = cAppend[i + 1][j] ^ temp[j];
            }
        }

        // convert c into array of long
        long[][] res = new long[c.length][2];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 8; k++) {
                    res[i][j] <<= 8;
                    res[i][j] |= (cAppend[i][j * 8 + k] & 0xFF);
                }
            }
        }

        return res;
    }
}
