package net.hssco.club;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.device.MagManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.hssco.club.data.model.Payment;
import net.hssco.club.data.model.TransactionTypeIntent;
import net.hssco.club.data.purchase.PurchaseImpl;

public class WelcomeActivity extends Activity {

    private static final String PREFS_NAME      = "sajed_prefs";
    private static final String KEY_SERVER_ADDR = "server_addr";
    private static final String KEY_SERVER_PORT = "server_port";
    private TextView statusTv;
    private MagManager magManager;
    private MagThread magThread;

    private TextView txtmerchantId;
    private TextView txtmerchantname;
    private String currentCard = "";

    private static final int MSG_CARD = 1;
    private static final int MSG_STATUS = 2;
    private static final int REQUEST_BANK_PAYMENT = 2001;

    private boolean isWifiConnected() {
        try {
            android.net.ConnectivityManager cm =
                    (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            android.net.NetworkInfo wifi =
                    cm.getNetworkInfo(android.net.ConnectivityManager.TYPE_WIFI);

            return wifi != null && wifi.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    private String getBaseUrl() {
        android.content.SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String addr = prefs.getString(KEY_SERVER_ADDR, "192.168.0.2");
        String port = prefs.getString(KEY_SERVER_PORT, "5212");

        addr = addr != null ? addr.trim() : "";
        port = port != null ? port.trim() : "";

        // اگر کاربر اشتباهاً پورت را خالی گذاشته بود
        if (addr.length() == 0) {
            addr = "192.168.0.2";
        }
        if (port.length() == 0) {
            port = "5212";
        }

        if (addr.startsWith("http://") || addr.startsWith("https://")) {
            return addr.endsWith("/") ? addr.substring(0, addr.length() - 1) : addr;
        }

        return "http://" + addr + ":" + port;
    }
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_CARD:
                    String pan = msg.getData().getString("PAN");
                    currentCard = pan;
                    Intent i = new Intent(WelcomeActivity.this, SelectOperationActivity.class);
                    i.putExtra("PAN", currentCard);
                    startActivity(i);
                    finish();
                    break;

                case MSG_STATUS:
                    statusTv.setText(msg.getData().getString("T"));

                    break;
            }
        }
    };

    private String parseMerchantId(String json) {
        if (json == null) return null;
        try {
            int idx = json.indexOf("\"merchantId\"");
            if (idx == -1) return null;

            idx = json.indexOf(":", idx);
            if (idx == -1) return null;

            int start = idx + 1;
            int end = start;

            while (end < json.length() &&
                    Character.isDigit(json.charAt(end))) {
                end++;
            }

            return json.substring(start, end).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String parseMerchantName(String json) {
        if (json == null) return null;
        try {
            int idx = json.indexOf("\"merchantName\"");
            if (idx == -1) return null;

            idx = json.indexOf(":", idx);
            if (idx == -1) return null;

            int firstQuote = json.indexOf("\"", idx);
            if (firstQuote == -1) return null;

            int secondQuote = json.indexOf("\"", firstQuote + 1);
            if (secondQuote == -1) return null;

            return json.substring(firstQuote + 1, secondQuote);
        } catch (Exception e) {
            return null;
        }
    }

    private void loadMerchantInfoFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = getBaseUrl();
                    String urlStr = baseUrl + "/api/Pos/GetMerchantInformation?id=1";

                    java.net.URL url = new java.net.URL(urlStr);
                    java.net.HttpURLConnection conn =
                            (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("accept", "text/plain");

                    java.io.InputStream in = conn.getInputStream();
                    java.io.BufferedReader reader =
                            new java.io.BufferedReader(new java.io.InputStreamReader(in, "UTF-8"));

                    final String json = reader.readLine();
                    reader.close();

                    // مثال JSON:
                    // { "merchantId": 253254, "merchantName": "سالن کنعانی" }

                    final String merchantId = parseMerchantId(json);
                    final String merchantName = parseMerchantName(json);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (merchantId != null && merchantId.length() > 0) {
                                txtmerchantId.setText("شماره پایانه : " + merchantId);
                            }

                            if (merchantName != null && merchantName.length() > 0) {
                                txtmerchantname.setText(merchantName);
                            }
                        }
                    });

                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // اگر دوست داشتی پیغام خطا هم نشان بده
                            // Toast.makeText(WelcomeActivity.this,
                            //         "خطا در دریافت اطلاعات پذیرنده",
                            //         Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    private void loadMerchantInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseUrl = getBaseUrl();
                    String urlStr = baseUrl + "/api/Pos/GetMerchantInformation?id=1";

                    java.net.URL url = new java.net.URL(urlStr);

                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("accept", "text/plain");

                    java.io.InputStream in = conn.getInputStream();
                    java.io.BufferedReader reader =
                            new java.io.BufferedReader(new java.io.InputStreamReader(in, "UTF-8"));
                    final String json = reader.readLine();
                    reader.close();

                    // این‌جا json رو parse کن (merchantId, merchantName)
                    // و روی متن‌ها بگذار
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // فرض کنیم بعداً parseش کردی
                            // txtmerchantId.setText("شماره پایانه : " + merchantId);
                            // txtmerchantname.setText(merchantName);
                        }
                    });

                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WelcomeActivity.this,
                                    "خطا در ارتباط با سرور",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();


    }



    private String toPersianDigits(String input) {
        if (input == null) return "";
        return input
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");
    }

    private void openBankPayment() {
        try {
            Intent intent = PurchaseImpl.getInstance().createEmptyIntent();
            startActivityForResult(intent, REQUEST_BANK_PAYMENT);
        } catch (Exception e) {
            Toast.makeText(this, "امکان اجرای پرداخت وجود ندارد", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_welcome);

        LinearLayout btnmerchantmenu = (LinearLayout) findViewById(R.id.btnmerchantmenu);
        LinearLayout btnadminmenu = (LinearLayout) findViewById(R.id.btnadminmenu);
        LinearLayout btnBankPayment = findViewById(R.id.btnBankPayment);
        btnadminmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, AdminPinActivity.class);
                i.putExtra("TARGET_MENU", "ADMIN");
                startActivity(i);
            }
        });


        btnmerchantmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, AdminPinActivity.class);
                i.putExtra("TARGET_MENU", "MERCHANT");
                startActivity(i);
            }
        });

        btnBankPayment.setOnClickListener(v -> openBankPayment());






        statusTv = findViewById(R.id.txtStatus);

        TextView txtSub2 = findViewById(R.id.txtSub2);
        TextView txtTime = findViewById(R.id.txtTime);
        txtmerchantname = findViewById(R.id.txtmerchantname);
        txtmerchantId = findViewById(R.id.txtmerchantId);

        loadMerchantInfo();

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date());
        String persianTime = toPersianDigits(time);
        txtTime.setText(persianTime);


        IranianCalendar ir = new IranianCalendar();
        int year = ir.getIranianYear();
        int month = ir.getIranianMonth();
        int day = ir.getIranianDay();


        String rawDate = String.format("%04d/%02d/%02d", year, month, day);


        String persianDate = toPersianDigits(rawDate);

        txtSub2.setText(persianDate);
        magManager = new MagManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // اگر وایفا وصله، اطلاعات پذیرنده را از سرور بگیر
        if (isWifiConnected()) {
            loadMerchantInfoFromServer();
        }

        // بقیه کد فعلی‌ات (مثلاً استارت کارتخوان)
        magThread = new MagThread();
        magThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (magThread != null) magThread.stopThread();
        if (magManager != null) magManager.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BANK_PAYMENT) {
            Payment payment = PurchaseImpl.getInstance().receiveResult(data);

            if (payment != null && payment.getResult() == 0) {
                Toast.makeText(this, "پرداخت با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "پرداخت ناموفق بود", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ---------------- Reader Thread ---------------------
    private class MagThread extends Thread {

        boolean running = true;

        void stopThread() { running = false; }

        @Override
        public void run() {

            int ret = magManager.open();
            if (ret != 0) {
                sendStatus("خطا در کارت‌خوان");
                return;
            }

            while (running) {

                ret = magManager.checkCard();

                if (ret != 0) {
                    sendStatus("لطفا کارت بکشید");
                    sleepMs(400);
                    continue;
                }

                sendStatus("در حال خواندن کارت...");

                byte[] buf = new byte[1024];
                int len = magManager.getAllStripInfo(buf);

                if (len > 0) {

                    StringBuffer track = new StringBuffer();

                    int len1 = buf[1];
                    if (len1 > 0)
                        track.append(new String(buf, 2, len1));

                    int len2 = buf[3 + len1];
                    if (len2 > 0)
                        track.append(new String(buf, 4 + len1, len2));

                    String digits = track.toString().replaceAll("[^0-9]", "");

                    if (digits.length() >= 16) {
                        String pan = digits.substring(0, 16);

                        Message m = uiHandler.obtainMessage(MSG_CARD);
                        Bundle b = new Bundle();
                        b.putString("PAN", pan);
                        m.setData(b);
                        uiHandler.sendMessage(m);
                    }
                }

                sleepMs(600);
            }
        }

        private void sendStatus(String t) {
            Message m = uiHandler.obtainMessage(MSG_STATUS);
            Bundle b = new Bundle();
            b.putString("T", t);
            m.setData(b);
            uiHandler.sendMessage(m);
        }

        private void sleepMs(int ms) {
            try { Thread.sleep(ms); } catch (Exception e) {}
        }
    }
}
