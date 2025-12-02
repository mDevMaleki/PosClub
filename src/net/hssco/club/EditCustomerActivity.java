package net.hssco.club;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.hssco.club.NavigationHelper;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditCustomerActivity extends Activity {

    private EditText edtName;
    private EditText edtFamily;
    private EditText edtMobile;
    private EditText edtBirthDay;
    private EditText edtNationalCode;
    private EditText edtTier;

    private Button btnCancel;
    private Button btnSave;

    private static final String PREFS_NAME      = "sajed_prefs";
    private static final String KEY_SERVER_ADDR = "server_addr";
    private static final String KEY_SERVER_PORT = "server_port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        edtName         = (EditText) findViewById(R.id.edtName);
        edtFamily       = (EditText) findViewById(R.id.edtFamily);
        edtMobile       = (EditText) findViewById(R.id.edtMobile);
        edtBirthDay     = (EditText) findViewById(R.id.edtBirthDay);
        edtNationalCode = (EditText) findViewById(R.id.edtNationalCode);
        edtTier         = (EditText) findViewById(R.id.edtTier);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave   = (Button) findViewById(R.id.btnSave);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.goToWelcome(EditCustomerActivity.this);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdate();
            }
        });
    }

    private String getBaseUrl() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String addr = prefs.getString(KEY_SERVER_ADDR, "192.168.0.2");
        String port = prefs.getString(KEY_SERVER_PORT, "5212");

        if (addr == null || addr.trim().length() == 0) {
            addr = "192.168.0.2";
        }
        if (port == null || port.trim().length() == 0) {
            port = "5212";
        }

        return "http://" + addr + ":" + port;
    }

    private void sendUpdate() {

        final String name   = edtName.getText().toString().trim();
        final String family = edtFamily.getText().toString().trim();
        final String mobile = edtMobile.getText().toString().trim();
        final String birth  = edtBirthDay.getText().toString().trim();
        final String nat    = edtNationalCode.getText().toString().trim();
        final String tierStr= edtTier.getText().toString().trim();

        if (name.length() == 0 || family.length() == 0) {
            Toast.makeText(this, "نام و نام خانوادگی الزامی است", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    String baseUrl = getBaseUrl();
                    String urlStr = baseUrl + "/api/Pos/UpdateCustomer";

                    URL url = new URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("accept", "text/plain");
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    conn.setDoOutput(true);

                    JSONObject obj = new JSONObject();
                    obj.put("name",          name);
                    obj.put("family",        family);
                    obj.put("nationalCode",  nat);
                    obj.put("mobile",        mobile);
                    obj.put("birthDay",      birth);
                    int tier = 0;
                    try {
                        if (tierStr.length() > 0) {
                            tier = Integer.parseInt(tierStr);
                        }
                    } catch (Exception e) { tier = 0; }
                    obj.put("tiarCustomer",  tier);

                    String json = obj.toString();

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(json);
                    writer.flush();
                    writer.close();
                    os.close();

                    int code = conn.getResponseCode();

                    final boolean ok = (code >= 200 && code < 300);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ok) {
                                Toast.makeText(EditCustomerActivity.this,
                                        "اطلاعات مشتری با موفقیت ثبت شد",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditCustomerActivity.this,
                                        "خطا در ثبت اطلاعات مشتری",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditCustomerActivity.this,
                                    "اشکال در ارتباط با سرور",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }
        }).start();
    }
}
