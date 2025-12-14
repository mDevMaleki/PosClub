package net.hssco.club;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.hssco.club.NavigationHelper;

import java.security.MessageDigest;

public class ChangeAdminPinActivity extends Activity {

    private EditText edtCurrent;
    private EditText edtNewPin;
    private EditText edtRepeat;

    private SharedPreferences prefs;

    private static final String PREFS_NAME = "sajed_prefs";
    private static final String KEY_ADMIN_PIN_HASH = "admin_pin_hash";
    private static final String KEY_MERCHANT_PIN_HASH = "merchant_pin_hash";

    private static final String DEFAULT_ADMIN_PIN = "1234";
    private static final String DEFAULT_MERCHANT_PIN = "1234";

    // نقش فعلی
    private String targetMenu = "ADMIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_admin_pin);

        // ببین برای کدام نقش آمده‌ای
        String t = getIntent().getStringExtra("TARGET_MENU");
        if (t != null && t.length() > 0) {
            targetMenu = t;
        }

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        edtCurrent = (EditText) findViewById(R.id.edtCurrentPin);
        edtNewPin = (EditText) findViewById(R.id.edtNewPin);
        edtRepeat = (EditText) findViewById(R.id.edtRepeatPin);

        Button btnSave = (Button) findViewById(R.id.btnSavePin);
        Button btnCancel = (Button) findViewById(R.id.btnCancelPin);

        // عنوان را بر اساس نقش عوض کن (اختیاری، ولی قشنگ‌تر است)
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        if ("MERCHANT".equalsIgnoreCase(targetMenu)) {
            txtTitle.setText("تغییر رمز پذیرنده");
        } else {
            txtTitle.setText("تغییر رمز سرپرست");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChangePin();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.goToWelcome(ChangeAdminPinActivity.this);
            }
        });
    }

    private void doChangePin() {
        String currentRaw = edtCurrent.getText().toString().trim();
        String newRaw = edtNewPin.getText().toString().trim();
        String repeatRaw = edtRepeat.getText().toString().trim();

        String current = toEnglishDigits(currentRaw);
        String newPin = toEnglishDigits(newRaw);
        String repeat = toEnglishDigits(repeatRaw);

        if (newPin.length() == 0 || repeat.length() == 0) {
            Toast.makeText(this, "رمز جدید را کامل وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPin.equals(repeat)) {
            Toast.makeText(this, "رمز جدید و تکرار آن یکسان نیست", Toast.LENGTH_SHORT).show();
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
        boolean currentOk = false;

        if (storedHash == null || storedHash.trim().length() == 0) {
            // هنوز برای این نقش رمزی ذخیره نشده → پس فقط رمز پیش‌فرض فعلی درست است
            if (defaultPin.equals(current)) {
                currentOk = true;
            }
        } else {
            String currentHash = hashPin(current);
            if (currentHash != null && currentHash.equalsIgnoreCase(storedHash)) {
                currentOk = true;
            } else if (storedHash.equals(current)) {
                // اگر قبلا PIN خام ذخیره شده باشد
                currentOk = true;
            }
        }

        if (!currentOk) {
            Toast.makeText(this, "رمز فعلی صحیح نیست", Toast.LENGTH_SHORT).show();
            return;
        }

        String newHash = hashPin(newPin);
        if (newHash == null) {
            Toast.makeText(this, "خطا در تولید رمز", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putString(hashKey, newHash).apply();

        Toast.makeText(this, "رمز با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
        finish();
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
