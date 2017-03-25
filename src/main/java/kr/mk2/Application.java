package kr.mk2;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        SimpleOTPService service = new SimpleOTPService();
        String userName = "moon";
        String host = "ian.mk2.kr";
        String secretKey = service.generateOTPSecretKey();
        String QRCodeUrl = service.generateQRCodeUrl(userName, host, secretKey);
        System.out.println(String.format("generated secret key : %s, generated url : %s", secretKey, QRCodeUrl));
        System.out.println("your next step is register secret key to google otp(https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2)");


        Scanner scanner = new Scanner(System.in);
        System.out.print("type generated OTP password : ");
        Integer otpPassword = scanner.nextInt();
        System.out.println(String.format("verify result : %s", service.verify(otpPassword, secretKey)));
    }
}
