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
import net.hssco.club.NavigationHelper;
import net.hssco.club.data.model.Payment;
import net.hssco.club.data.model.TransactionTypeIntent;
import net.hssco.club.data.purchase.PurchaseImpl;
import net.hssco.club.sdk.PspApiClient;
import net.hssco.club.sdk.api.PspApiService;
import net.hssco.club.sdk.model.LocalRequestClubCardChargeCommand;
import net.hssco.club.sdk.model.LocalRequestClubCardChargeResult;
import net.hssco.club.sdk.model.VerifyLocalRequestClubCardChargeCommand;
import net.hssco.club.sdk.model.VerifyLocalRequestClubCardChargResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AmountActivity extends Activity {


    private TextView amountInput;
    private String pan;
    private final StringBuilder amountBuilder = new StringBuilder();

    private static final String PREFS_NAME       = "sajed_prefs";
    private static final String KEY_SERVER_ADDR  = "server_addr";
    private static final String KEY_SERVER_PORT  = "server_port";
    private static final String KEY_TERMINAL_ID  = "terminal_id";
    private static final String KEY_LICENSE      = "license";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {

            Payment payment = PurchaseImpl.getInstance().receiveResult(data);
            if (payment != null && payment.getResult() == 0) {
                pan = payment.getCardNumber();
                requestCharge(payment);
            } else {
                Intent fail = new Intent(AmountActivity.this, PaymentResultActivity.class);
                fail.putExtra("status", "fail");
                fail.putExtra("type", "charge");
                fail.putExtra("message", payment != null ? payment.getMessage() : "پرداخت ناموفق");
                startActivity(fail);
                finish();
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
                NavigationHelper.goToWelcome(AmountActivity.this);
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

    private void requestCharge(final Payment payment) {

        PspApiService service = createApiService();
        if (service == null) {
            Toast.makeText(this, "آدرس سرور نامعتبر است", Toast.LENGTH_SHORT).show();
            return;
        }

        final String stan = generateStan();
        long amountValue = parseAmount(amountBuilder.toString());

        LocalRequestClubCardChargeCommand command = new LocalRequestClubCardChargeCommand(
                System.currentTimeMillis(),
                100,
                getLicense(),
                "INIT",
                "Charge",
                getTodayDate(),
                getCurrentTime(),
                amountValue,
                "REF" + stan,
                getTerminalId(),
                stan,
                payment.getCardNumber(),
                "VALID",
                "0000",
                "ANDROID",
                "0000",
                "123",
                "Club charge",
                "charge payload",
                payment.getCardNumber()
        );

        service.chargeClubCard(command).enqueue(new Callback<LocalRequestClubCardChargeResult>() {
            @Override
            public void onResponse(Call<LocalRequestClubCardChargeResult> call,
                                   Response<LocalRequestClubCardChargeResult> response) {

                if (response.isSuccessful() && response.body() != null) {
                    verifyCharge(stan, response.body());
                } else {
                    openChargeResult(false, null, "پاسخ شارژ معتبر نیست");
                }
            }

            @Override
            public void onFailure(Call<LocalRequestClubCardChargeResult> call, Throwable t) {
                openChargeResult(false, null, t.getMessage());
            }
        });
    }

    private void verifyCharge(final String stan, final LocalRequestClubCardChargeResult chargeResult) {

        PspApiService service = createApiService();
        if (service == null) {
            openChargeResult(false, null, "آدرس سرور نامعتبر است");
            return;
        }

        VerifyLocalRequestClubCardChargeCommand verifyCommand = new VerifyLocalRequestClubCardChargeCommand(
                System.currentTimeMillis(),
                100,
                getTerminalId(),
                stan
        );

        service.verifyCharge(verifyCommand).enqueue(new Callback<VerifyLocalRequestClubCardChargResult>() {
            @Override
            public void onResponse(Call<VerifyLocalRequestClubCardChargResult> call,
                                   Response<VerifyLocalRequestClubCardChargResult> response) {

                boolean success = response.isSuccessful();
                String message = chargeResult != null ? chargeResult.getSpOutputMessage() : null;
                openChargeResult(success, chargeResult, message);
            }

            @Override
            public void onFailure(Call<VerifyLocalRequestClubCardChargResult> call, Throwable t) {
                openChargeResult(false, chargeResult, t.getMessage());
            }
        });
    }

    private void openChargeResult(boolean success, LocalRequestClubCardChargeResult result,
                                  String message) {

        Intent intent = new Intent(AmountActivity.this, PaymentResultActivity.class);
        intent.putExtra("status", success ? "success" : "fail");
        intent.putExtra("type", "charge");
        intent.putExtra("amount", amountBuilder.toString());
        intent.putExtra("card", pan);
        intent.putExtra("terminal", getTerminalId());
        intent.putExtra("tracking", result != null ? result.getAccTableVersion() : null);
        intent.putExtra("message", message);
        startActivity(intent);
        finish();
    }

    private PspApiService createApiService() {
        try {
            String base = getBaseUrl();
            if (!base.endsWith("/")) {
                base = base + "/";
            }
            return PspApiClient.create(base).getApiService();
        } catch (Exception e) {
            return null;
        }
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

    private String getTerminalId() {
        android.content.SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getString(KEY_TERMINAL_ID, "TERM001");
    }

    private String getLicense() {
        android.content.SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getString(KEY_LICENSE, "MERCHANT_PIN");
    }

    private String getTodayDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US);
        return sdf.format(new java.util.Date());
    }

    private String getCurrentTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.US);
        return sdf.format(new java.util.Date());
    }

    private String generateStan() {
        int value = new java.util.Random().nextInt(900000) + 100000;
        return String.valueOf(value);
    }

    private long parseAmount(String amountString) {
        try {
            if (amountString == null) return 0L;
            String clean = amountString
                    .replaceAll(",", "")
                    .replace("۰", "0")
                    .replace("۱", "1")
                    .replace("۲", "2")
                    .replace("۳", "3")
                    .replace("۴", "4")
                    .replace("۵", "5")
                    .replace("۶", "6")
                    .replace("۷", "7")
                    .replace("۸", "8")
                    .replace("۹", "9");
            return Long.parseLong(clean);
        } catch (Exception e) {
            return 0L;
        }
    }

}
