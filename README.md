# Payment Notification Inspector

A minimal Android Studio project that captures notification fields for PhonePe, Google Pay (India), and Paytm, then displays them inside the app.

## Important limitations
- This is source code, not a compiled APK or Android App Bundle (.aab).
- Notification contents vary by app, version, device, and transaction type.
- Package names may vary; confirm installed package names on your target phone.
- This app reads notification fields exposed by Android. It does not access bank records and cannot independently prove a payment succeeded.
- A payment note, receiving UPI ID, or transaction reference is only available if the payment app exposes it in notification data.
- Notification access can expose sensitive personal/payment information. Use only on a device you own or are authorized to administer, keep the data private, and clear test data after testing.
- The sample stores up to 200 notification records locally for debugging. It does not send data to a server.

## Build in Android Studio
1. Install Android Studio with a compatible JDK (JDK 17 recommended).
2. Open this project folder.
3. Let Gradle sync.
4. Connect an Android 14 device and install the app using Run.
5. Open the app and tap **Enable notification access**.
6. Enable **Payment Notification Inspector** in Android Settings.
7. Make test payments/receive test notifications, return to the app, and tap **Refresh captured notifications**.

## Build an Android App Bundle (.aab)
In Android Studio, choose **Build > Generate Signed Bundle / APK > Android App Bundle**. Create or select a signing key and follow the wizard. For local testing, an APK is usually easier to install directly.

## Package names used in this sample
- PhonePe: `com.phonepe.app`
- Google Pay India: `com.google.android.apps.nbu.paisa.user`
- Paytm: `net.one97.paytm` and `com.paytm`

These are common package identifiers, not a guarantee for every device or app build. If a package is different on your phone, update `targetPackages` in `PaymentNotificationListener.kt`.

## Privacy
Captured notifications may contain names, phone numbers, payment details, or other sensitive data. This sample keeps records locally in app preferences. Do not use it to monitor another person's notifications without their informed permission.
