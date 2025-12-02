package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerPinActivity extends Activity {

    private TextView txtPin;
    private StringBuilder pinBuilder = new StringBuilder();

    private String pan;
    private String amount;
    private String mode;

    // shared prefs keys
    private static final String PREFS_NAME      = "sajed_prefs";
    private static final String KEY_SERVER_ADDR = "server_addr";
    private static final String KEY_SERVER_PORT = "server_port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_pin);

        txtPin = (TextView) findViewById(R.id.txtPin);

        pan    = getIntent().getStringExtra("pan");
        amount = getIntent().getStringExtra("amount");
        mode   = getIntent().getStringExtra("mode");

        // دکمه‌های عددی
        int[] numIds = new int[] {
                R.id.btnNum1, R.id.btnNum2, R.id.btnNum3,
                R.id.btnNum4, R.id.btnNum5, R.id.btnNum6,
                R.id.btnNum7, R.id.btnNum8, R.id.btnNum9,
                R.id.btnNum0
        };

        View.OnClickListener numClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (pinBuilder.length() >= 6) return;
                pinBuilder.append(b.getText().toString());
                updatePinView();
            }
        };

        for (int i = 0; i < numIds.length; i++) {
            Button b = (Button) findViewById(numIds[i]);
            b.setOnClickListener(numClick);
        }

        // بک‌اسپیس
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
                finish();
            }
        });

        // تایید
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pin = pinBuilder.toString();

                if (pin.length() == 0) {
                    Toast.makeText(CustomerPinActivity.this,
                            "رمز مشتری را وارد کنید",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // رشته کامل برای بک‌اند
                String msg = mode + "|" + pan + "|" + amount + "|" + pin;

                sendPostMessage(msg);
            }
        });


    }

    private void updatePinView() {
        StringBuilder stars = new StringBuilder();
        int len = pinBuilder.length();
        for (int i = 0; i < len; i++) {
            stars.append("•");
        }
        txtPin.setText(stars.toString());
    }

    private String getBaseUrl() {

        android.content.SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String addr = prefs.getString(KEY_SERVER_ADDR, "192.168.0.2");
        String port = prefs.getString(KEY_SERVER_PORT, "5212");

        if (addr == null || addr.trim().length() == 0)
            addr = "192.168.0.2";

        if (port == null || port.trim().length() == 0)
            port = "5212";

        return "http://" + addr + ":" + port;
    }

    private void sendPostMessage(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = getBaseUrl() + "/api/Pos/PosMessage?message=" + message;

                    java.net.URL url = new java.net.URL(urlStr);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("accept", "application/json");

                    int code = conn.getResponseCode();

                    java.io.InputStream is = conn.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    final String json = s.hasNext() ? s.next() : "";

                    conn.disconnect();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(CustomerPinActivity.this, PaymentResultActivity.class);
                            i.putExtra("json", json);
                            startActivity(i);
                            finish();
                        }
                    });

                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerPinActivity.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

}
