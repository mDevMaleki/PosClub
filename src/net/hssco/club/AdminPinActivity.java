package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import net.hssco.club.NavigationHelper;

public class AdminPinActivity extends Activity {

    private TextView txtPin;
    private StringBuilder pinBuilder = new StringBuilder();

    private SharedPreferences prefs;

    // اسم فایل تنظیمات
    private static final String PREFS_NAME = "sajed_prefs";
    // کلیدهای جدا برای دو نقش
    private static final String KEY_ADMIN_PIN_HASH = "admin_pin_hash";
    private static final String KEY_MERCHANT_PIN_HASH = "merchant_pin_hash";

    // رمز پیش‌فرض اولیه برای هر کدام
    private static final String DEFAULT_ADMIN_PIN = "1234";
    private static final String DEFAULT_MERCHANT_PIN = "1234";

    // مقصد / نقش: ADMIN یا MERCHANT
    private String targetMenu = "ADMIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pin);

        // از Intent بگیر که برای کدام منو آمده‌ای
        String t = getIntent().getStringExtra("TARGET_MENU");
        if (t != null && t.length() > 0) {
            targetMenu = t;
        }

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        txtPin = (TextView) findViewById(R.id.txtPin);

        // دکمه‌های عددی
        int[] numIds = {
                R.id.btnNum1, R.id.btnNum2, R.id.btnNum3,
                R.id.btnNum4, R.id.btnNum5, R.id.btnNum6,
                R.id.btnNum7, R.id.btnNum8, R.id.btnNum9,
                R.id.btnNum0
        };

        View.OnClickListener numClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (pinBuilder.length() >= 6) return; // حداکثر طول PIN
                pinBuilder.append(b.getText().toString()); // ممکنه فارسی باشه
                updatePinView();
            }
        };

        int i;
        for (i = 0; i < numIds.length; i++) {
            Button b = (Button) findViewById(numIds[i]);
            b.setOnClickListener(numClick);
        }

        // دکمه پاک کردن
        ImageButton btnBackspace = (ImageButton) findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinBuilder.length() > 0) {
                    pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                    updatePinView();
                }
            }
        });

        // لغو
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.goToWelcome(AdminPinActivity.this);
            }
        });

        // تایید
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pinRaw = pinBuilder.toString();      // مثلا "۱۲۳۴"
                String pin = toEnglishDigits(pinRaw);       // تبدیل به "1234"

                if (pin.length() == 0) {
                    Toast.makeText(AdminPinActivity.this,
                            "رمز را وارد کنید",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // بر اساس نقش، کلید و رمز پیش‌فرض را انتخاب کن
                String hashKey;
                String defaultPin;
                if ("MERCHANT".equalsIgnoreCase(targetMenu)) {
                    hashKey = KEY_MERCHANT_PIN_HASH;
                    defaultPin = DEFAULT_MERCHANT_PIN;
                } else {
                    hashKey = KEY_ADMIN_PIN_HASH;
                    defaultPin = DEFAULT_ADMIN_PIN;
                }

                String storedHash = prefs.getString(hashKey, null);
                boolean ok = false;
                boolean firstTime = false;

                if (storedHash == null || storedHash.trim().length() == 0) {
                    // هنوز برای این نقش رمزی تنظیم نشده → فقط رمز پیش‌فرض قبول است
                    if (defaultPin.equals(pin)) {
                        ok = true;
                        firstTime = true;   // اولین بار → بفرست به تغییر رمز این نقش
                    }
                } else {
                    String enteredHash = hashPin(pin);

                    // حالت نرمال: هش درست ذخیره شده
                    if (enteredHash != null && enteredHash.equalsIgnoreCase(storedHash)) {
                        ok = true;
                    }
                    // اگر قبلا اشتباهی خود PIN خام ذخیره شده باشد
                    else if (storedHash.equals(pin)) {
                        ok = true;
                    }
                }

                if (ok) {
                    if (firstTime) {
                        // اولین ورود → باید رمز همین نقش تنظیم شود
                        Intent i = new Intent(AdminPinActivity.this, ChangeAdminPinActivity.class);
                        i.putExtra("TARGET_MENU", targetMenu); // نقش را هم بفرست
                        startActivity(i);
                        finish();
                    } else {
                        // ورود عادی
                        Intent i;
                        if ("MERCHANT".equalsIgnoreCase(targetMenu)) {
                            i = new Intent(AdminPinActivity.this, MerchantMenuActivity.class);
                        } else {
                            i = new Intent(AdminPinActivity.this, AdminMenuActivity.class);
                        }
                        startActivity(i);
                        finish();
                    }
                } else {
                    Toast.makeText(AdminPinActivity.this,
                            "رمز نادرست است",
                            Toast.LENGTH_SHORT).show();
                    pinBuilder.setLength(0);
                    updatePinView();
                }
            }
        });
    }

    private void updatePinView() {
        // نمایش • به‌جای رقم واقعی
        StringBuilder stars = new StringBuilder();
        int i;
        for (i = 0; i < pinBuilder.length(); i++) {
            stars.append("•");
        }
        txtPin.setText(stars.toString());
    }

    private String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(pin.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            int i;
            for (i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02x", bytes[i] & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // تبدیل اعداد فارسی/عربی به انگلیسی
    private String toEnglishDigits(String input) {
        if (input == null) return "";
        return input
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9")
                .replace("٠", "0")
                .replace("١", "1")
                .replace("٢", "2")
                .replace("٣", "3")
                .replace("٤", "4")
                .replace("٥", "5")
                .replace("٦", "6")
                .replace("٧", "7")
                .replace("٨", "8")
                .replace("٩", "9");
    }

    @Override
    public void onBackPressed() {
        NavigationHelper.goToWelcome(this);
    }
}
