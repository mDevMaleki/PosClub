package net.hssco.club;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.device.PrinterManager;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.hssco.club.data.model.Payment;

public class PaymentResultActivity extends Activity {

    private TextView txtTitle, txtSubTitle;
    private TextView txtClub, txtBranch, txtAmount, txtDate, txtTime,
            txtTracking, txtTerminal, txtMerchant, txtCard, txtSupport;

    private ImageView imgStatus;

    private ImageView imgLogo;
    private RelativeLayout boxBackground;

    private PrinterManager printerManager;

    private boolean isSuccess = false;
    private boolean isCharge = false;
    private boolean isBalance = false;

    private String amountText;
    private String cardText;
    private String terminalText;
    private String trackingText;
    private String merchantText;
    private String customMessage;

    private String supportNumber = "021-225542544";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        initViews();
        setupPrinter();
        loadData();
        setupButtons();
    }

    private void initViews() {
        txtTitle     = (TextView) findViewById(R.id.txtTitle);
        txtSubTitle  = (TextView) findViewById(R.id.txtSubTitle);

        txtClub      = (TextView) findViewById(R.id.txtClub);
        txtBranch    = (TextView) findViewById(R.id.txtBranch);
        txtAmount    = (TextView) findViewById(R.id.txtAmount);
        txtDate      = (TextView) findViewById(R.id.txtDate);
        txtTime      = (TextView) findViewById(R.id.txtTime);
        txtTracking  = (TextView) findViewById(R.id.txtTracking);
        txtTerminal  = (TextView) findViewById(R.id.txtTerminal);
        txtMerchant  = (TextView) findViewById(R.id.txtMerchant);
        txtCard      = (TextView) findViewById(R.id.txtCard);
        txtSupport   = (TextView) findViewById(R.id.txtSupport);

        imgStatus    = (ImageView) findViewById(R.id.imgStatus);
        imgLogo    = (ImageView) findViewById(R.id.imgLogo);
        boxBackground = (RelativeLayout) findViewById(R.id.relBackground);
    }

    private void setupPrinter() {
        try {
            printerManager = new PrinterManager();
            printerManager.open();
        } catch (Exception e) {
            Toast.makeText(this, "پرینتر در دسترس نیست", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {

        String json = getIntent().getStringExtra("json");
        String status = getIntent().getStringExtra("status");
        String type   = getIntent().getStringExtra("type");

        amountText   = getIntent().getStringExtra("amount");
        cardText     = getIntent().getStringExtra("card");
        terminalText = getIntent().getStringExtra("terminal");
        trackingText = getIntent().getStringExtra("tracking");
        merchantText = getIntent().getStringExtra("merchant");
        customMessage = getIntent().getStringExtra("message");

        if ("charge".equals(type)) {
            isCharge = true;
        }

        if ("balance".equals(type)) {
            isBalance = true;
        }

        if ("success".equalsIgnoreCase(status)) {
            isSuccess = true;
        }

        if (json != null) {
            try {
                Payment p = new Gson().fromJson(json, Payment.class);

                amountText = p.getTotalAmount();
                trackingText = p.getRrn();
                terminalText = p.getTerminalId();
                merchantText = p.getMerchantName();
                cardText = p.getCardNumber();

            } catch (Exception e) {
                Toast.makeText(this, "خطا در پردازش نتیجه", Toast.LENGTH_SHORT).show();
            }
        }

        applyDataToViews();

        applyUI();
    }

    private void applyDataToViews() {

        txtClub.setText("باشگاه مشتریان کنعانی");
        txtBranch.setText("سالن کنعانی");

        if (amountText != null) {
            txtAmount.setText(amountText);
        }

        if (trackingText != null) {
            txtTracking.setText(trackingText);
        }

        if (terminalText != null) {
            txtTerminal.setText(terminalText);
        }

        if (merchantText != null) {
            txtMerchant.setText(merchantText);
        }

        if (cardText != null) {
            txtCard.setText(cardText);
        }

        txtSupport.setText(supportNumber);

        if (txtDate.getText().toString().trim().isEmpty()) {
            txtDate.setText(getTodayDate());
        }

        if (txtTime.getText().toString().trim().isEmpty()) {
            txtTime.setText(getCurrentTime());
        }
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void applyUI() {

        if (isSuccess) {

            if (isBalance) {
                txtTitle.setText("استعلام موجودی موفق");
                txtSubTitle.setText("");
            } else if (isCharge) {
                txtTitle.setText("کارت با موفقیت شارژ شد");
                txtSubTitle.setText(customMessage != null ? customMessage : "");
            } else {
                txtTitle.setText("پرداخت موفق");
                txtSubTitle.setText(customMessage != null ? customMessage : "");
            }

            imgStatus.setImageResource(R.drawable.recsuccess);
            imgLogo.setImageResource(R.drawable.reslogo);
            boxBackground.setBackgroundResource(R.drawable.bg_green_gradient);

        } else {

            txtTitle.setText("عملیات ناموفق");
            if (customMessage != null) {
                txtSubTitle.setText(customMessage);
            } else {
                txtSubTitle.setText("رمز وارد شده صحیح نمی‌باشد");
            }

            imgStatus.setImageResource(R.drawable.resfail);
            imgLogo.setImageResource(R.drawable.reslogofail);
            // اگر میخوای drawable رو اعمال کنی
            boxBackground.setBackgroundResource(R.drawable.bg_main_gradient);




        }
    }

    private void setupButtons() {

        Button btnBack = (Button) findViewById(R.id.btnBack);
        Button btnPrint = (Button) findViewById(R.id.btnPrint);

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        btnPrint.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        printReceipt();
                    }
                }
        );
    }


    private void printReceipt() {

        if (printerManager == null) {
            Toast.makeText(this, "پرینتر در دسترس نیست", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            String header;
            if (isBalance) {
                header = "   رسید استعلام موجودی\n";
            } else if (isCharge) {
                header = "   رسید افزایش موجودی\n";
            } else {
                header = "       رسید خرید موفق\n";
            }

            String txt =
                    header +
                            "-------------------------------\n" +
                            "باشگاه: باشگاه مشتریان کنعانی\n" +
                            "شعبه: سالن کنعانی\n" +
                            "مبلغ: " + txtAmount.getText().toString() + "\n" +
                            "تاریخ: " + txtDate.getText().toString() +
                            "   ساعت: " + txtTime.getText().toString() + "\n" +
                            "پایانه: " + txtTerminal.getText().toString() + "\n" +
                            "پذیرنده: " + txtMerchant.getText().toString() + "\n" +
                            "شماره پیگیری:\n" + txtTracking.getText().toString() + "\n" +
                            "شماره کارت:\n" + txtCard.getText().toString() + "\n" +
                            "-------------------------------\n" +
                            "شماره امداد مشتریان: " + supportNumber + "\n\n";

            Bitmap bmp = PersianBitmapPrinter.textToBitmapRTL(this, txt, 32);

            printerManager.setupPage(384, -1);
            printerManager.drawBitmap(bmp, 0, 0);
            printerManager.printPage(0);
            printerManager.paperFeed(80);

        } catch (Exception e) {
            Toast.makeText(this, "خطا در چاپ رسید", Toast.LENGTH_SHORT).show();
        }
    }
}
