package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.hssco.club.sdk.PspApiClient;
import net.hssco.club.sdk.api.PspApiService;
import net.hssco.club.sdk.model.GetBalanceRequestTransactionCommand;
import net.hssco.club.sdk.model.GetBalanceRequestTransactionResult;
import net.hssco.club.sdk.model.GetBalanceResponse;
import net.hssco.club.sdk.model.PspSaleRequestTransactionCommand;
import net.hssco.club.sdk.model.PspSaleRequestTransactionResult;
import net.hssco.club.sdk.model.PspSaleResponse;
import net.hssco.club.sdk.model.PspVerifySaleRequestTransactionCommand;
import net.hssco.club.sdk.model.PspVerifySaleRequestTransactionResult;
import net.hssco.club.sdk.model.PspVerifySaleResponse;

import java.security.MessageDigest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerPinActivity extends Activity {

    private static final String TAG = "CustomerPinActivity";

    private TextView txtPin;
    private StringBuilder pinBuilder = new StringBuilder();

    private String pan;
    private String amount;
    private String mode;

    private static final String PREFS_NAME      = "sajed_prefs";
    private static final String KEY_SERVER_ADDR = "server_addr";
    private static final String KEY_SERVER_PORT = "server_port";
    private static final String KEY_TERMINAL_ID = "terminal_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_pin);

        txtPin = findViewById(R.id.txtPin);

        pan    = getIntent().getStringExtra("pan");
        amount = getIntent().getStringExtra("amount");
        mode   = getIntent().getStringExtra("mode");

        Log.d(TAG, "onCreate - pan=" + pan + " mode=" + mode + " amount=" + amount);

        setupNumberButtons();
        setupBackspaceButton();
        setupCancelButton();
        setupOkButton();
    }

    private void setupNumberButtons() {
        int[] numIds = new int[]{
                R.id.btnNum1, R.id.btnNum2, R.id.btnNum3,
                R.id.btnNum4, R.id.btnNum5, R.id.btnNum6,
                R.id.btnNum7, R.id.btnNum8, R.id.btnNum9,
                R.id.btnNum0
        };

        View.OnClickListener numClick = v -> {
            Button b = (Button) v;
            if (pinBuilder.length() >= 6) return;
            pinBuilder.append(b.getText().toString());
            updatePinView();
        };

        for (int id : numIds) findViewById(id).setOnClickListener(numClick);
    }

    private void setupBackspaceButton() {
        ImageButton btnBackspace = findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(v -> {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                updatePinView();
            }
        });
    }

    private void setupCancelButton() {
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> NavigationHelper.goToWelcome(CustomerPinActivity.this));
    }

    private void setupOkButton() {
        Button btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {
            String pin = pinBuilder.toString();
            if (pin.isEmpty()) {
                Toast.makeText(this, "رمز مشتری را وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("balance".equals(mode)) {
                requestBalance(pin);
            } else {
                requestSale(pin);
            }
        });
    }

    private void updatePinView() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < pinBuilder.length(); i++) stars.append("•");
        txtPin.setText(stars.toString());
    }

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

    private String normalizeDigits(String input) {
        if (input == null) return null;
        return input.replace("۰","0").replace("۱","1").replace("۲","2")
                .replace("۳","3").replace("۴","4").replace("۵","5")
                .replace("۶","6").replace("۷","7").replace("۸","8")
                .replace("۹","9");
    }

    // ================== PSP API Calls ==================

    private void requestBalance(String pin) {
        PspApiService service = createApiService();
        if (service == null) { showError("آدرس سرور نامعتبر است"); return; }

        String normalizedPin = normalizeDigits(pin);

        GetBalanceRequestTransactionCommand command = new GetBalanceRequestTransactionCommand(
                System.currentTimeMillis(),
                100,
                "INIT",
                "0920",
                getTodayDate(),
                getCurrentTime(),
                getTerminalId(),
                generateStan(),
                sha1(pan),
                sha1(normalizedPin),
                "POS balance request"
        );

        service.getBalance(command).enqueue(new Callback<GetBalanceResponse>() {
            @Override
            public void onResponse(Call<GetBalanceResponse> call, Response<GetBalanceResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getData() == null) {
                    openResultScreen(false,"balance",null,"خطای شبکه یا پاسخ نامعتبر",null);
                    return;
                }

                GetBalanceRequestTransactionResult result = response.body().getData();
                String status = result.getResponsStatus() != null ? result.getResponsStatus().trim() : "unknown";
                boolean success = "0000".equals(status);
                String message = getPersianMessageForStatus(status);

                openResultScreen(success,"balance",String.valueOf(result.getAmount()),message,new Gson().toJson(result));
            }

            @Override
            public void onFailure(Call<GetBalanceResponse> call, Throwable t) {
                openResultScreen(false,"balance",null,"عدم اتصال به سرور: "+t.getMessage(),null);
            }
        });
    }

    private void requestSale(final String pin) {
        PspApiService service = createApiService();
        if (service == null) { showError("آدرس سرور نامعتبر است"); return; }

        final String stan = generateStan();
        final long amountValue = parseAmount(amount);
        String pinToSend = sha1(normalizeDigits(pin));

        PspSaleRequestTransactionCommand command = new PspSaleRequestTransactionCommand(
                System.currentTimeMillis(),
                100,
                "INIT",
                "0860",
                getTodayDate(),
                getCurrentTime(),
                amountValue,
                "REF"+stan,
                getTerminalId(),
                stan,
                sha1(pan),
                "VALID",
                pinToSend,
                "ANDROID",
                "POS sale",
                "sale request"
        );

        Log.d(TAG, "Sale request body: pan=" + pan + " amount=" + amountValue + " pin(hash)=" + pinToSend);

        service.sale(command).enqueue(new Callback<PspSaleResponse>() {
            @Override
            public void onResponse(Call<PspSaleResponse> call, Response<PspSaleResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    openResultScreen(false,"buy",null,"پاسخ خرید معتبر نیست",null);
                    return;
                }

                PspSaleResponse resp = response.body();
                PspSaleRequestTransactionResult saleResult = resp.getData();
                if (saleResult == null) {
                    openResultScreen(false,"buy",String.valueOf(amountValue),"پاسخ خرید خالی است",null);
                    return;
                }

                String saleStatus = saleResult.getResponsStatus() != null ? saleResult.getResponsStatus().trim() : "unknown";

                if ("0000".equals(saleStatus)) {
                    verifySale(stan, amountValue, saleResult);
                } else {
                    openResultScreen(false,"buy",String.valueOf(amountValue),getPersianMessageForStatus(saleStatus),null);
                }
            }

            @Override
            public void onFailure(Call<PspSaleResponse> call, Throwable t) {
                openResultScreen(false,"buy",null,"عدم اتصال به سرور: "+t.getMessage(),null);
            }
        });
    }

    private void verifySale(final String stan, final long amountValue, final PspSaleRequestTransactionResult saleResult) {
        PspApiService service = createApiService();
        if (service == null) { openResultScreen(false,"buy",null,"آدرس سرور نامعتبر است",null); return; }

        PspVerifySaleRequestTransactionCommand verifyCommand = new PspVerifySaleRequestTransactionCommand(
                System.currentTimeMillis(),
                100,
                getTerminalId(),
                stan
        );

        service.verifySale(verifyCommand).enqueue(new Callback<PspVerifySaleResponse>() {
            @Override
            public void onResponse(Call<PspVerifySaleResponse> call, Response<PspVerifySaleResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    openResultScreen(false,"buy",String.valueOf(amountValue),"پاسخ تایید نامعتبر است",null);
                    return;
                }

                PspVerifySaleResponse resp = response.body();
                PspVerifySaleRequestTransactionResult verifyBody = resp.getData();
                if (verifyBody == null) {
                    openResultScreen(false,"buy",String.valueOf(amountValue),"پاسخ تایید خالی است",null);
                    return;
                }

                String verifyStatus = verifyBody.getResponsStatus() != null ? verifyBody.getResponsStatus().trim() : "unknown";
                boolean success = "0000".equals(verifyStatus);
                openResultScreen(success,"buy",String.valueOf(amountValue),getPersianMessageForStatus(verifyStatus),null);
            }

            @Override
            public void onFailure(Call<PspVerifySaleResponse> call, Throwable t) {
                openResultScreen(false,"buy",String.valueOf(amountValue),"عدم اتصال به سرور تایید: "+t.getMessage(),null);
            }
        });
    }

    private void openResultScreen(boolean success, String type, String amountValue, String message, String tracking) {
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
            if (!base.endsWith("/")) base = base + "/";
            return PspApiClient.create(base).getApiService();
        } catch (Exception e) {
            return null;
        }
    }

    private String getBaseUrl() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String addr = prefs.getString(KEY_SERVER_ADDR, "192.168.1.110");
        String port = prefs.getString(KEY_SERVER_PORT, "5212");
        addr = addr != null ? addr.trim() : "";
        port = port != null ? port.trim() : "";
        if (addr.isEmpty()) addr = "192.168.1.110";
        if (port.isEmpty()) port = "5212";
        if (addr.startsWith("http://") || addr.startsWith("https://")) return addr;
        return "http://" + addr + ":" + port;
    }

    private String getTerminalId() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
            String clean = normalizeDigits(amountString.replaceAll(",", ""));
            return Long.parseLong(clean);
        } catch (Exception e) {
            return 0L;
        }
    }

    private void showError(final String message) {
        runOnUiThread(() -> Toast.makeText(CustomerPinActivity.this,message,Toast.LENGTH_LONG).show());
    }

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
            default:     return "خطای ناشناخته ("+status+")";
        }
    }

    @Override
    public void onBackPressed() {
        NavigationHelper.goToWelcome(this);
    }
}
