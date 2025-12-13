package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.hssco.club.data.model.Payment;
import net.hssco.club.data.model.TransactionTypeIntent;
import net.hssco.club.data.purchase.PurchaseImpl;
import net.hssco.club.sdk.PspApiClient;
import net.hssco.club.sdk.api.PspApiService;
import net.hssco.club.sdk.model.AddBalanceResponse;
import net.hssco.club.sdk.model.AddBalanceWithPanCommand;
import net.hssco.club.sdk.model.LocalRequestClubCardChargeCommand;
import net.hssco.club.sdk.model.LocalRequestClubCardChargeResult;
import net.hssco.club.sdk.model.VerifyLocalRequestClubCardChargeCommand;
import net.hssco.club.sdk.model.VerifyLocalRequestClubCardChargResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AmountActivity extends Activity {

    private TextView amountInput;
    private String pan; // PAN برای تراکنش‌ها
    private final StringBuilder amountBuilder = new StringBuilder();
    private String mode; // "charge" یا "buy"

    private long chargeAmount;

    private static final String PREFS_NAME = "sajed_prefs";
    private static final String KEY_SERVER_ADDR = "server_addr";
    private static final String KEY_SERVER_PORT = "server_port";
    private static final String KEY_TERMINAL_ID = "terminal_id";
    private static final String KEY_LICENSE = "license";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        amountInput = findViewById(R.id.amountInput);
        Button btnNext = findViewById(R.id.btnNext);

        // دریافت mode و PAN از Intent
        mode = getIntent().getStringExtra("mode");
        pan = getIntent().getStringExtra("PAN");

        setupNumberButtons();
        setupBackspaceButton();
        setupCancelButton();

        btnNext.setOnClickListener(v -> onNextClicked());
    }

    private void setupNumberButtons() {
        int[] numIds = {
                R.id.btnNum1, R.id.btnNum2, R.id.btnNum3,
                R.id.btnNum4, R.id.btnNum5, R.id.btnNum6,
                R.id.btnNum7, R.id.btnNum8, R.id.btnNum9,
                R.id.btnNum0
        };

        View.OnClickListener numClick = v -> {
            Button b = (Button) v;
            amountBuilder.append(b.getText().toString());
            updateAmountView();
        };

        for (int id : numIds) {
            findViewById(id).setOnClickListener(numClick);
        }
    }

    private void setupBackspaceButton() {
        ImageButton btnBackspace = findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(v -> {
            if (amountBuilder.length() > 0) {
                amountBuilder.deleteCharAt(amountBuilder.length() - 1);
                updateAmountView();
            }
        });
    }

    private void setupCancelButton() {
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());
    }

    private void onNextClicked() {
        String amountStr = amountInput.getText().toString().trim();

        if (amountStr.isEmpty() || amountStr.equals("0")) {
            amountInput.setError("مبلغ معتبر نیست");
            return;
        }

        if ("charge".equals(mode)) {
            // شارژ → ابتدا تراکنش POS
            Payment payment = new Payment();
            payment.setApplicationId("1");
            payment.setTotalAmount(amountBuilder.toString());
            payment.setTransactionType(TransactionTypeIntent.PAYMENT);

            Intent intent = PurchaseImpl.getInstance().createIntent(payment);
            startActivityForResult(intent, 1001);

        } else if ("buy".equals(mode)) {
            // خرید → PAN باید از Intent آمده باشد
            if (pan == null || pan.isEmpty()) {
                Toast.makeText(this, "شماره کارت موجود نیست", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(AmountActivity.this, CustomerPinActivity.class);
            i.putExtra("mode", "buy");
            i.putExtra("pan", pan);
            i.putExtra("amount", amountStr);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            Payment payment = PurchaseImpl.getInstance().receiveResult(data);

            if (payment != null && payment.getResult() == 0) {
                // پرداخت موفق → اجرا کردن شارژ و وریفای
                pan = payment.getCardNumber();
                requestCharge(payment);

            } else {
                // پرداخت ناموفق → مستقیم رفتن به صفحه نتیجه
                openChargeResult(false, payment != null ? payment.getMessage() : "پرداخت ناموفق");
            }
        }
    }

    private void updateAmountView() {
        TextView txtWords = findViewById(R.id.txtWords);

        if (amountBuilder.length() == 0) {
            amountInput.setText("");
            txtWords.setText("");
            return;
        }

        try {
            long value = parseAmount(amountBuilder.toString());
            String formatted = String.format("%,d", value);
            formatted = toPersianDigits(formatted);
            amountInput.setText(formatted);
            txtWords.setText(numberToPersianWords(value) + " ریال");
        } catch (Exception e) {
            amountInput.setText("");
            txtWords.setText("");
        }
    }

    // ----------- شارژ کارت -------------
    private void requestCharge(final Payment payment) {
        PspApiService service = createApiService();
        if (service == null) {
            Toast.makeText(this, "آدرس سرور نامعتبر است", Toast.LENGTH_SHORT).show();
            return;
        }

        final String stan = generateStan();
        chargeAmount = parseAmount(amountBuilder.toString());

        LocalRequestClubCardChargeCommand command = new LocalRequestClubCardChargeCommand(
                System.currentTimeMillis(),
                100,
                getLicense(),
                "INIT",
                "Charge",
                getTodayDate(),
                getCurrentTime(),
                chargeAmount,
                "REF" + stan,
                getTerminalId(),
                stan,
                pan,
                "VALID",
                "0000",
                "ANDROID",
                "0000",
                "123",
                "Club charge",
                "charge payload",
                pan
        );

        service.chargeClubCard(command).enqueue(new Callback<LocalRequestClubCardChargeResult>() {
            @Override
            public void onResponse(Call<LocalRequestClubCardChargeResult> call,
                                   Response<LocalRequestClubCardChargeResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    verifyCharge(stan, response.body());
                } else {
                    openChargeResult(false, "پاسخ شارژ معتبر نیست");
                }
            }

            @Override
            public void onFailure(Call<LocalRequestClubCardChargeResult> call, Throwable t) {
                openChargeResult(false, t.getMessage());
            }
        });
    }

    private void verifyCharge(final String stan, final LocalRequestClubCardChargeResult chargeResult) {
        PspApiService service = createApiService();
        if (service == null) {
            openChargeResult(false, "آدرس سرور نامعتبر است");
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
                VerifyLocalRequestClubCardChargResult verifyBody = response.body();
                String verifyStatus = verifyBody != null ? verifyBody.getResponsStatus() : null;

                boolean success = response.isSuccessful() && "0000".equals(verifyStatus);
                String message = getPersianMessageForStatus(verifyStatus);

                // اگر وریفای موفق نبود ولی پاسخ شارژ اولیه داشتیم، پیام اولیه را نمایش بدهیم
                if (!success && chargeResult != null) {
                    if (chargeResult.getSpOutputMessage() != null && !chargeResult.getSpOutputMessage().trim().isEmpty()) {
                        message = chargeResult.getSpOutputMessage();
                    } else if (chargeResult.getResponsStatus() != null) {
                        message = getPersianMessageForStatus(chargeResult.getResponsStatus());
                    }
                }

                if (success) {
                    sendAddBalanceToBackend(message);
                } else {
                    openChargeResult(false, message);
                }
            }

            @Override
            public void onFailure(Call<VerifyLocalRequestClubCardChargResult> call, Throwable t) {
                openChargeResult(false, t.getMessage());
            }
        });
    }

    private void sendAddBalanceToBackend(final String fallbackMessage) {
        PspApiService service = createApiService();
        if (service == null) {
            openChargeResult(false, "آدرس سرور نامعتبر است");
            return;
        }

        AddBalanceWithPanCommand request = new AddBalanceWithPanCommand(pan, chargeAmount);
        service.addBalance(request).enqueue(new Callback<AddBalanceResponse>() {
            @Override
            public void onResponse(Call<AddBalanceResponse> call, Response<AddBalanceResponse> response) {
                AddBalanceResponse body = response.body();
                if (response.isSuccessful() && body != null) {
                    boolean success = body.isSuccess();
                    String message = body.getMessage();
                    if (message == null || message.trim().isEmpty()) {
                        message = fallbackMessage != null ? fallbackMessage : "ثبت شارژ با موفقیت انجام شد";
                    }
                    openChargeResult(success, message);
                } else {
                    openChargeResult(false, "پاسخ ثبت شارژ معتبر نیست");
                }
            }

            @Override
            public void onFailure(Call<AddBalanceResponse> call, Throwable t) {
                openChargeResult(false, t.getMessage());
            }
        });
    }

    private void openChargeResult(boolean success, String message) {
        Intent intent = new Intent(AmountActivity.this, PaymentResultActivity.class);
        intent.putExtra("status", success ? "success" : "fail");
        intent.putExtra("type", "charge");
        intent.putExtra("amount", amountBuilder.toString());
        intent.putExtra("card", pan);
        intent.putExtra("message", message);
        startActivity(intent);
        finish();
    }

    private PspApiService createApiService() {
        try {
            String base = getBaseUrl();
            if (!base.endsWith("/")) base += "/";
            return PspApiClient.create(base).getApiService();
        } catch (Exception e) {
            return null;
        }
    }

    private String getBaseUrl() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String addr = prefs.getString(KEY_SERVER_ADDR, "192.168.0.2");
        String port = prefs.getString(KEY_SERVER_PORT, "5212");

        addr = addr != null ? addr.trim() : "";
        port = port != null ? port.trim() : "";

        if (!addr.startsWith("http://") && !addr.startsWith("https://")) {
            addr = "http://" + addr + ":" + port;
        }

        return addr;
    }

    private String getTerminalId() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_TERMINAL_ID, "TERM001");
    }

    private String getLicense() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        if (amountString == null) return 0L;
        try {
            String clean = amountString.replaceAll(",", "")
                    .replace("۰", "0").replace("۱", "1")
                    .replace("۲", "2").replace("۳", "3")
                    .replace("۴", "4").replace("۵", "5")
                    .replace("۶", "6").replace("۷", "7")
                    .replace("۸", "8").replace("۹", "9");
            return Long.parseLong(clean);
        } catch (Exception e) {
            return 0L;
        }
    }

    private String toPersianDigits(String text) {
        return text.replace("0", "۰").replace("1", "۱").replace("2", "۲")
                .replace("3", "۳").replace("4", "۴").replace("5", "۵")
                .replace("6", "۶").replace("7", "۷").replace("8", "۸")
                .replace("9", "۹");
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
                if (d == 1) section.append(dah[y]).append(" ");
                else {
                    if (d > 1) section.append(dahgan[d]).append(" ");
                    if (y > 0) section.append(yekan[y]).append(" ");
                }

                if (!groups[groupIndex].isEmpty()) section.append(groups[groupIndex]).append(" ");
                if (result.length() > 0) result.insert(0, " و ");
                result.insert(0, section.toString());
            }
            number /= 1000;
            groupIndex++;
        }

        return result.toString().trim();
    }

    // ----------- پیغام فارسی برای کد پاسخ PSP -------------
    private String getPersianMessageForStatus(String status) {
        if (status == null) return "خطای نامعلوم";

        switch (status.trim()) {
            case "0000": return "عملیات موفق";
            case "0001": return "ترمینال نامعتبر یا غیرفعال است";
            case "0002": return "شعبه نامعتبر یا غیرفعال است";
            case "0003": return "کارت یا صاحب کارت نامعتبر است";
            case "0004": return "کارت غیرفعال یا منقضی شده";
            case "0007": return "رمز کارت اشتباه است";
            case "0011": return "موجودی کافی نیست";
            default:     return "خطای ناشناخته (" + status + ")";
        }
    }
}
