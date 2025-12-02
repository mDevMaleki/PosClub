package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import net.hssco.club.data.model.Payment;
import net.hssco.club.data.model.TransactionTypeIntent;
import net.hssco.club.data.purchase.PurchaseImpl;

public class AmountActivity extends Activity {


    private TextView amountInput;
    private String pan;
    private final StringBuilder amountBuilder = new StringBuilder();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {

            Payment payment = PurchaseImpl.getInstance().receiveResult(data);
            if (payment != null && payment.getResult() == 0) {

                String msg = "charge|"
                        + pan + "|"
                        + amountBuilder.toString() + "|"
                        + payment.getRrn() + "|"
                        + payment.getCardNumber() + "|"
                        + payment.getTerminalId();

                callAddBalance(msg);
            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);
        String pin = amountBuilder.toString();
        amountInput = (TextView) findViewById(R.id.amountInput);
        Button btnNext = (Button) findViewById(R.id.btnNext);

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
                amountBuilder.append(b.getText().toString());
                updatePinView();
            }
        };

        int i;
        for (i = 0; i < numIds.length; i++) {
            Button b = (Button) findViewById(numIds[i]);
            b.setOnClickListener(numClick);
        }

        // بک‌اسپیس
        ImageButton btnBackspace = (ImageButton) findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountBuilder.length() > 0) {
                    amountBuilder.deleteCharAt(amountBuilder.length() - 1);
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





        pan = getIntent().getStringExtra("pan");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = amountInput.getText().toString().trim();

                if (amount.equals("") || amount.equals("0")) {
                    amountInput.setError("مبلغ معتبر نیست");
                    return;
                }

                final String mode = getIntent().getStringExtra("mode");

                if ("charge".equals(mode)) {
                    // 🔵 افزایش موجودی → OmidPayment
                    Payment payment = new Payment();
                    payment.setApplicationId("1");
                    payment.setTotalAmount(amountBuilder.toString());
                    payment.setPurchaseId("");
                    payment.setTransactionType(
                            TransactionTypeIntent.PAYMENT);

                    Intent intent = PurchaseImpl.getInstance().createIntent(payment);
                    startActivityForResult(intent, 1001);
                    return;
                }

                // 🟢 خرید → ادامه مسیر فعلی
                Intent i = new Intent(AmountActivity.this, CustomerPinActivity.class);
                i.putExtra("mode", "buy");
                i.putExtra("pan", pan);
                i.putExtra("amount", amount);
                startActivity(i);
            }
        });

    }
    private String convertPaymentToJson(Payment p) {
        Gson g = new Gson();
        return g.toJson(p);
    }
    public static String numberToPersianWords(long number) {
        if (number == 0) return "صفر";

        String[] yekan = {"", "یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه"};
        String[] dahgan = {"", "ده", "بیست", "سی", "چهل", "پنجاه", "شصت", "هفتاد", "هشتاد", "نود"};
        String[] sadgan = {"", "صد", "دویست", "سیصد", "چهارصد", "پانصد", "ششصد", "هفتصد", "هشتصد", "نهصد"};
        String[] dah = {"ده", "یازده", "دوازده", "سیزده", "چهارده", "پانزده", "شانزده", "هفده", "هجده", "نوزده"};
        String[] groups = {"", "هزار", "میلیون", "میلیارد", "تریلیون"};

        StringBuilder result = new StringBuilder();
        int groupIndex = 0;

        while (number > 0) {
            int part = (int) (number % 1000);

            if (part != 0) {
                StringBuilder section = new StringBuilder();

                int s = part / 100;
                int d = (part % 100) / 10;
                int y = part % 10;

                if (s != 0) section.append(sadgan[s]).append(" ");

                if (d == 1) {
                    section.append(dah[y]).append(" ");
                } else {
                    if (d > 1) section.append(dahgan[d]).append(" ");
                    if (y > 0) section.append(yekan[y]).append(" ");
                }

                if (!groups[groupIndex].isEmpty())
                    section.append(groups[groupIndex]).append(" ");

                if (result.length() > 0)
                    result.insert(0, " و ");

                result.insert(0, section.toString());
            }

            number /= 1000;
            groupIndex++;
        }

        return result.toString().trim();
    }
    private String toPersianDigits(String text) {
        return text
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
    private void updatePinView() {
        TextView txtWords = findViewById(R.id.txtWords);

        try {
            String raw = amountBuilder.toString();

            if (raw.isEmpty()) {
                amountInput.setText("");
                txtWords.setText("");
                return;
            }

            long value = Long.parseLong(raw);

            // سه رقمی انگلیسی
            String formatted = String.format("%,d", value);

            // تبدیل به فارسی
            formatted = toPersianDigits(formatted);

            amountInput.setText(formatted);

            // تبدیل به حروف فارسی
            String words = numberToPersianWords(value);
            txtWords.setText(words + " ریال");

        } catch (Exception e) {
            amountInput.setText("");
            txtWords.setText("");
        }
    }

    private void callAddBalance(final String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "http://192.168.1.110:5212/api/Pos/AddBalance?message=" + message;

                    java.net.URL url = new java.net.URL(urlStr);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setRequestProperty("accept", "text/plain");

                    int code = conn.getResponseCode();
                    java.io.InputStream is = conn.getInputStream();

                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    final String result = s.hasNext() ? s.next() : "";

                    conn.disconnect();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("true".equalsIgnoreCase(result.trim())) {
                                Intent ok = new Intent(AmountActivity.this, PaymentResultActivity.class);
                                ok.putExtra("status", "success");
                                ok.putExtra("type", "charge");
                                startActivity(ok);
                            } else {
                                Intent fail = new Intent(AmountActivity.this, PaymentResultActivity.class);
                                fail.putExtra("status", "fail");
                                fail.putExtra("type", "charge");
                                startActivity(fail);
                            }
                            finish();
                        }
                    });

                } catch (final Exception e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AmountActivity.this,
                                    "خطا در ارتباط با سرور",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }


}
