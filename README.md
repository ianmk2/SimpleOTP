Simple OTP server side example for JAVA
===


Generating OTP secret key
---
```
SimpleOTPService service = new SimpleOTPService();
String userName = "moon";
String host = "ian.mk2.kr";
String secretKey = service.generateOTPSecretKey();
String QRCodeUrl = service.generateQRCodeUrl(userName, host, secretKey);
```

Verifying
---
```
boolean result = service.verify(otpPassword, secretKey);
```