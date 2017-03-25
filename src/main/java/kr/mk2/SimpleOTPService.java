package kr.mk2;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class SimpleOTPService {

    // https://www.javacodegeeks.com/2011/12/google-authenticator-using-it-with-your.html/comment-page-1/#comment-14663
    // https://github.com/wstrange/GoogleAuth


    /**
     * Window is used to check codes generated in the near past.
     * You can use this value to tune how far you're willing to go.
     */
    private static final int WINDOW_SIZE = 1;

    public String generateOTPSecretKey() {
        int SECRET_BITS = 80;
        int NUMBER_OF_SCRATCH = 5;
        int SCRATCH_CODE_SIZE = 4;
        byte[] buffer = new byte[SECRET_BITS / 8 + NUMBER_OF_SCRATCH * SCRATCH_CODE_SIZE];
        new Random().nextBytes(buffer);
        byte[] secretKey = Arrays.copyOf(buffer, SECRET_BITS / 8);
        return new String(new Base32().encode(secretKey));
    }

    public String generateQRCodeUrl(String user, String host, String secretKey) {
        return String.format("https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s", user, host, secretKey);
    }

    public boolean verify(int otpPassword, String secretKey) {
        try {
            return checkCode(secretKey, otpPassword, new Date().getTime() / 30000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkCode(String secret, long code, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] decodedKey = new Base32().decode(secret);
        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
            long hash = verifyCode(decodedKey, t + i);
            if (hash == code)
                return true;
        }
        return false;
    }

    private int verifyCode(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

}
