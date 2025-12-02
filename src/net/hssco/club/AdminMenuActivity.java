package net.hssco.club;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// پرینتر
import android.device.PrinterManager;
import net.hssco.club.NavigationHelper;

public class AdminMenuActivity extends Activity {

    private EditText edtTerminalId;
    private EditText edtSerial;
    private EditText edtLicense;
    private EditText edtServerAddress;
    private EditText edtServerPort;

    private Button btnWifi;
    private Button btnPrintSettings;
    private Button btnChangePin;
    private Button btnSave;
    private Button btnCancel;

    private SharedPreferences prefs;
    private PrinterManager printerManager;

    private static final String PREFS_NAME       = "sajed_prefs";
    private static final String KEY_TERMINAL_ID  = "terminal_id";
    private static final String KEY_LICENSE      = "license";
    private static final String KEY_SERVER_ADDR  = "server_addr";
    private static final String KEY_SERVER_PORT  = "server_port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        edtTerminalId    = (EditText) findViewById(R.id.edtTerminalId);
        edtSerial        = (EditText) findViewById(R.id.edtSerial);
        edtLicense       = (EditText) findViewById(R.id.edtLicense);
        edtServerAddress = (EditText) findViewById(R.id.edtServerAddress);
        edtServerPort    = (EditText) findViewById(R.id.edtServerPort);

        btnWifi          = (Button) findViewById(R.id.btnWifi);
        btnPrintSettings = (Button) findViewById(R.id.btnPrintSettings);
        btnChangePin     = (Button) findViewById(R.id.btnChangePin);
        btnSave          = (Button) findViewById(R.id.btnSave);
        btnCancel        = (Button) findViewById(R.id.btnCancel);

        // سریال دستگاه را از سیستم بگیر و نمایش بده (غیر قابل ویرایش در XML)
        edtSerial.setText(getDeviceSerial());

        // تنظیمات ذخیره‌شده را لود کن
        loadSettings();

        // راه‌اندازی پرینتر
        try {
            printerManager = new PrinterManager();
            printerManager.open();
        } catch (Throwable t) {
            Toast.makeText(this,
                    "خطا در باز کردن پرینتر",
                    Toast.LENGTH_SHORT).show();
        }

        // دکمه ذخیره
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(AdminMenuActivity.this,
                        "تنظیمات ذخیره شد",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // دکمه لغو
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.goToWelcome(AdminMenuActivity.this);
            }
        });

        // رفتن به تنظیمات وایفا
        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(i);
            }
        });

        // چاپ تنظیمات
        btnPrintSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printSettings();
            }
        });

        // تغییر رمز سرپرست
        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMenuActivity.this, ChangeAdminPinActivity.class);
                i.putExtra("TARGET_MENU", "ADMIN");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWifiButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (printerManager != null) {
                printerManager.close();
            }
        } catch (Throwable t) {
            // اهمیتی ندارد
        }
    }

    // به‌روزرسانی وضعیت دکمه وایفا
    private void updateWifiButton() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifi != null && wifi.isConnected()) {
                btnWifi.setText("وایفا وصل است");
            } else {
                btnWifi.setText("اتصال به وایفا");
            }
        } catch (Throwable t) {
            // بی‌خیال
        }
    }

    // گرفتن سریال دستگاه
    private String getDeviceSerial() {
        String serial = null;

        try {
            if (Build.VERSION.SDK_INT >= 26) {
                serial = Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
        } catch (Throwable e) {
            serial = null;
        }

        if (serial == null || "unknown".equalsIgnoreCase(serial)) {
            // fallback: ANDROID_ID
            serial = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
        }

        return serial != null ? serial : "";
    }

    // لود تنظیمات
    private void loadSettings() {
        String terminalId = prefs.getString(KEY_TERMINAL_ID, "");
        String license    = prefs.getString(KEY_LICENSE, "");
        String srvAddr    = prefs.getString(KEY_SERVER_ADDR, "");
        String srvPort    = prefs.getString(KEY_SERVER_PORT, "");

        edtTerminalId.setText(terminalId);
        edtLicense.setText(license);
        edtServerAddress.setText(srvAddr);
        edtServerPort.setText(srvPort);
        // edtSerial را از سیستم گرفتیم
    }

    // ذخیره تنظیمات
    private void saveSettings() {
        String terminalId = edtTerminalId.getText().toString().trim();
        String license    = edtLicense.getText().toString().trim();
        String srvAddr    = edtServerAddress.getText().toString().trim();
        String srvPort    = edtServerPort.getText().toString().trim();

        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(KEY_TERMINAL_ID, terminalId);
        ed.putString(KEY_LICENSE,    license);
        ed.putString(KEY_SERVER_ADDR,srvAddr);
        ed.putString(KEY_SERVER_PORT,srvPort);
        ed.apply();
    }

    // چاپ تنظیمات با پرینتر
    private void printSettings() {
        if (printerManager == null) {
            Toast.makeText(this,
                    "پرینتر در دسترس نیست",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // تازه‌ترین مقادیر را از UI بخوان
            String terminalId = edtTerminalId.getText().toString().trim();
            String serial     = edtSerial.getText().toString().trim();
            String license    = edtLicense.getText().toString().trim();
            String srvAddr    = edtServerAddress.getText().toString().trim();
            String srvPort    = edtServerPort.getText().toString().trim();

            printerManager.setupPage(384, -1);

            String receipt =
                    "تنظیمات دستگاه\n" +
                            "----------------------\n" +
                            "سریال دستگاه: " + serial + "\n" +
                            "شماره پایانه: " + terminalId + "\n" +
                            "کد لایسنس: " + license + "\n" +
                            "آدرس سرور: " + srvAddr + "\n" +
                            "پورت سرور: " + srvPort + "\n" +
                            "----------------------\n\n"+
                            "\n\n"
                            ;

            Bitmap bmp = PersianBitmapPrinter.textToBitmapRTL(this, receipt, 32);

// برای تست اگر دوست داشتی:
            if (bmp == null || bmp.getWidth() == 0 || bmp.getHeight() == 0) {
                Toast.makeText(this, "Bitmap پرینت نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }

            printerManager.setupPage(384, -1);
            printerManager.drawBitmap(bmp, 0, 0);
            printerManager.printPage(0);
            printerManager.paperFeed(80);

        } catch (Exception e) {
            Toast.makeText(this,
                    "خطا در چاپ تنظیمات",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
