package net.hssco.club;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Locale;

public class EditCustomerActivity extends Activity {

    private EditText edtName;
    private EditText edtFamily;
    private EditText edtMobile;
    private EditText edtBirthDay;
    private EditText edtNationalCode;
    private EditText edtTier;

    public Long memberId;

    private Button btnCancel;
    private Button btnSave;

    private long clubId = 0;
    private long branchId = 0;

    private static final String PREFS_NAME = "sajed_prefs";
    private static final String KEY_SERVER_ADDR = "server_addr";
    private static final String KEY_SERVER_PORT = "server_port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        edtName = findViewById(R.id.edtName);
        edtFamily = findViewById(R.id.edtFamily);
        edtMobile = findViewById(R.id.edtMobile);
        edtBirthDay = findViewById(R.id.edtBirthDay);
        edtNationalCode = findViewById(R.id.edtNationalCode);
        edtTier = findViewById(R.id.edtTier);

        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        // تنظیم کلیک‌شناس برای تقویم
        edtBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // غیرفعال کردن ویرایش دستی
        edtBirthDay.setFocusable(false);
        edtBirthDay.setFocusableInTouchMode(false);
        edtBirthDay.setClickable(true);

        String pan = getIntent().getStringExtra("PAN");
        if (pan != null && !pan.isEmpty()) {
            fetchCustomerData(pan);
        }

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> sendUpdate());
    }

    // نمایش دیالوگ تاریخ
    private void showDatePickerDialog() {
        // تاریخ فعلی
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // اگر تاریخ قبلی وجود دارد، آن را تنظیم کنیم
        String currentDate = edtBirthDay.getText().toString().trim();
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("-");
                if (parts.length == 3) {
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1; // ماه از 0 شروع می‌شود
                    day = Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ایجاد دیالوگ
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // ماه از 0 شروع می‌شود، پس +1 می‌کنیم
                    selectedMonth = selectedMonth + 1;

                    // فرمت تاریخ: YYYY-MM-DD
                    String formattedDate = String.format(Locale.US,
                            "%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay);

                    // نمایش تاریخ به صورت خوانا
                    String displayDate = String.format(Locale.US,
                            "%04d/%02d/%02d", selectedYear, selectedMonth, selectedDay);

                    edtBirthDay.setText(formattedDate);
                    edtBirthDay.setHint(displayDate);
                },
                year, month, day
        );

        // محدودیت‌های تاریخ (اختیاری)
        Calendar minDate = Calendar.getInstance();
        minDate.set(1300, 0, 1); // 1300-01-01
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(1450, 11, 31); // 1450-12-31
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.setTitle("انتخاب تاریخ تولد");
        datePickerDialog.show();
    }

    // تبدیل تاریخ میلادی به شمسی ساده (تقریبی)
    private String convertToPersian(String gregorianDate) {
        try {
            if (gregorianDate == null || gregorianDate.isEmpty()) {
                return "";
            }

            String[] parts = gregorianDate.split("-");
            if (parts.length == 3) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                // تبدیل تقریبی (برای نمایش)
                // این تبدیل دقیق نیست، فقط برای نمایش ساده است
                // برای تبدیل دقیق نیاز به الگوریتم واقعی دارید
                int persianYear = year - 621;
                return String.format(Locale.US, "%04d/%02d/%02d", persianYear, month, day);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gregorianDate;
    }

    // بقیه متدها بدون تغییر باقی می‌مانند...
    private String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(input.getBytes("US-ASCII"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02X", b));
            return sb.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    private String getBaseUrl() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String addr = prefs.getString(KEY_SERVER_ADDR, "192.168.0.2").trim();
        String port = prefs.getString(KEY_SERVER_PORT, "5212").trim();

        if (addr.startsWith("http://") || addr.startsWith("https://")) {
            return addr.endsWith("/") ? addr.substring(0, addr.length() - 1) : addr;
        }
        return "http://" + addr + ":" + port;
    }

    private void fetchCustomerData(String pan) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String urlStr = getBaseUrl() + "/api/psp/creditCard?Pan=" + sha1(pan);
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("accept", "application/json");

                int code = conn.getResponseCode();
                if (code >= 200 && code < 300) {
                    java.util.Scanner s = new java.util.Scanner(conn.getInputStream()).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    s.close();

                    JSONObject json = new JSONObject(result);
                    JSONObject data = json.getJSONObject("data");

                    runOnUiThread(() -> {
                        edtName.setText(data.optString("memberName", ""));
                        edtFamily.setText(data.optString("memberFamily", ""));
                        edtMobile.setText(data.optString("memberMobile", ""));

                        String birthDay = data.optString("memberBrithDay", "");
                        if (!birthDay.isEmpty()) {
                            edtBirthDay.setText(birthDay);
                            // نمایش تاریخ به صورت شمسی (تقریبی)
                            String persianDate = convertToPersian(birthDay);
                            if (!persianDate.isEmpty()) {
                                edtBirthDay.setHint(persianDate + " (شمسی)");
                            }
                        } else {
                            edtBirthDay.setHint("برای انتخاب کلیک کنید");
                        }

                        edtNationalCode.setText(data.optString("memberNationalCode", ""));
                        edtTier.setText(data.optString("memberTier", ""));
                        memberId = data.optLong("memberId", 0);
                        clubId = data.optLong("clubId", 0);
                        branchId = data.optLong("branchId", 0);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditCustomerActivity.this,
                        "خطا در دریافت اطلاعات مشتری", Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void sendUpdate() {
        final String name = edtName.getText().toString().trim();
        final String family = edtFamily.getText().toString().trim();
        final String mobile = edtMobile.getText().toString().trim();
        final String birth = edtBirthDay.getText().toString().trim();
        final String nat = edtNationalCode.getText().toString().trim();
        final String tierStr = edtTier.getText().toString().trim();

        if (name.isEmpty() || family.isEmpty()) {
            Toast.makeText(this, "نام و نام خانوادگی الزامی است", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String urlStr = getBaseUrl() + "/api/psp/member/" + memberId;
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);

                JSONObject obj = new JSONObject();
                obj.put("id", memberId);
                obj.put("firstName", name);
                obj.put("lastName", family);
                obj.put("mobile", mobile);
                obj.put("birthDay", birth);
                obj.put("nationalCode", nat);
                obj.put("membershipTierId", tierStr.isEmpty() ? 1 : Integer.parseInt(tierStr));
                obj.put("clubId", clubId);
                obj.put("branchId", branchId);

                String json = obj.toString();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.flush();
                writer.close();
                os.close();

                int code = conn.getResponseCode();
                final boolean ok = (code >= 200 && code < 300);

                runOnUiThread(() -> {
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
                });

            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditCustomerActivity.this,
                        "اشکال در ارتباط با سرور", Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}