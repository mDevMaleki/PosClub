package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import net.hssco.club.NavigationHelper;
import net.hssco.club.sdk.PspApiClient;
import net.hssco.club.sdk.api.PspApiService;
import net.hssco.club.sdk.model.GetBalanceRequestTransactionCommand;
import net.hssco.club.sdk.model.GetBalanceRequestTransactionResult;
import net.hssco.club.sdk.model.PspSaleRequestTransactionCommand;
import net.hssco.club.sdk.model.PspSaleRequestTransactionResult;
import net.hssco.club.sdk.model.PspVerifySaleRequestTransactionCommand;
import net.hssco.club.sdk.model.PspVerifySaleRequestTransactionResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private static final String KEY_TERMINAL_ID = "terminal_id";

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
                NavigationHelper.goToWelcome(CustomerPinActivity.this);
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

                if ("balance".equals(mode)) {
                    requestBalance(pin);
                } else {
                    requestSale(pin);
                }
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

    private void requestBalance(String pin) {

        PspApiService service = createApiService();

        if (service == null) {
            Toast.makeText(this, "آدرس سرور نامعتبر است", Toast.LENGTH_SHORT).show();
            return;
        }

        GetBalanceRequestTransactionCommand command = new GetBalanceRequestTransactionCommand(
                System.currentTimeMillis(),
                100,
                "INIT",
                "Balance",
                getTodayDate(),
                getCurrentTime(),
                getTerminalId(),
                generateStan(),
                pan,
                pin,
                "POS balance request"
        );

        service.getBalance(command).enqueue(new Callback<GetBalanceRequestTransactionResult>() {
            @Override
            public void onResponse(Call<GetBalanceRequestTransactionResult> call,
                                   Response<GetBalanceRequestTransactionResult> response) {

                if (response.isSuccessful() && response.body() != null) {
                    GetBalanceRequestTransactionResult result = response.body();
                    openResultScreen(
                            true,
                            "balance",
                            String.valueOf(result.getAmount()),
                            result.getSpOutputMessage(),
                            null
                    );
                } else {
                    openResultScreen(false, "balance", null,
                            "عدم دریافت پاسخ معتبر", null);
                }
            }

            @Override
            public void onFailure(Call<GetBalanceRequestTransactionResult> call, Throwable t) {
                openResultScreen(false, "balance", null, t.getMessage(), null);
            }
        });
    }

    private void requestSale(final String pin) {

        PspApiService service = createApiService();

        if (service == null) {
            Toast.makeText(this, "آدرس سرور نامعتبر است", Toast.LENGTH_SHORT).show();
            return;
        }

        final String stan = generateStan();
        final long amountValue = parseAmount(amount);

        PspSaleRequestTransactionCommand command = new PspSaleRequestTransactionCommand(
                System.currentTimeMillis(),
                100,
                "INIT",
                "Sale",
                getTodayDate(),
                getCurrentTime(),
                amountValue,
                "REF" + stan,
                getTerminalId(),
                stan,
                pan,
                "VALID",
                pin,
                "ANDROID",
                "0000",
                "123",
                "POS sale",
                "sale request"
        );

        service.sale(command).enqueue(new Callback<PspSaleRequestTransactionResult>() {
            @Override
            public void onResponse(Call<PspSaleRequestTransactionResult> call,
                                   Response<PspSaleRequestTransactionResult> response) {

                if (response.isSuccessful()) {
                    verifySale(stan, amountValue, response.body());
                } else {
                    openResultScreen(false, "buy", null, "پاسخ خرید معتبر نیست", null);
                }
            }

            @Override
            public void onFailure(Call<PspSaleRequestTransactionResult> call, Throwable t) {
                openResultScreen(false, "buy", null, t.getMessage(), null);
            }
        });
    }

    private void verifySale(final String stan, final long amountValue,
                            final PspSaleRequestTransactionResult saleResult) {

        PspApiService service = createApiService();
        if (service == null) {
            openResultScreen(false, "buy", null, "آدرس سرور نامعتبر است", null);
            return;
        }

        PspVerifySaleRequestTransactionCommand verifyCommand = new PspVerifySaleRequestTransactionCommand(
                System.currentTimeMillis(),
                100,
                getTerminalId(),
                stan
        );

        service.verifySale(verifyCommand).enqueue(new Callback<PspVerifySaleRequestTransactionResult>() {
            @Override
            public void onResponse(Call<PspVerifySaleRequestTransactionResult> call,
                                   Response<PspVerifySaleRequestTransactionResult> response) {

                boolean ok = response.isSuccessful();
                String message = saleResult != null ? saleResult.getSpOutputMessage() : null;
                String tracking = saleResult != null ? saleResult.getVerifyCode() : null;

                openResultScreen(ok, "buy", String.valueOf(amountValue), message, tracking);
            }

            @Override
            public void onFailure(Call<PspVerifySaleRequestTransactionResult> call, Throwable t) {
                openResultScreen(false, "buy", null, t.getMessage(), null);
            }
        });
    }

    private void openResultScreen(boolean success, String type, String amountValue,
                                  String message, String tracking) {

        Intent i = new Intent(CustomerPinActivity.this, PaymentResultActivity.class);
        i.putExtra("status", success ? "success" : "fail");
        i.putExtra("type", type);
        i.putExtra("amount", amountValue);
        i.putExtra("card", pan);
        i.putExtra("terminal", getTerminalId());
        i.putExtra("tracking", tracking);
        i.putExtra("message", message);
        startActivity(i);
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

        addr = addr != null ? addr.trim() : "";
        port = port != null ? port.trim() : "";

        if (addr.length() == 0) {
            addr = "192.168.0.2";
        }

        if (port.length() == 0) {
            port = "5212";
        }

        if (addr.startsWith("http://") || addr.startsWith("https://")) {
            // کاربر آدرس کامل را وارد کرده است
            return addr.endsWith("/") ? addr.substring(0, addr.length() - 1) : addr;
        }

        return "https://" + addr + ":" + port;
    }

    private String getTerminalId() {
        android.content.SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        return prefs.getString(KEY_TERMINAL_ID, "TERM001");
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
