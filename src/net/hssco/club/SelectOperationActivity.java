package net.hssco.club;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import net.hssco.club.NavigationHelper;

public class SelectOperationActivity extends AppCompatActivity {

    private View btnBuy;
    private View btnBalance;
    private View btnStatement;
    private View btnEditInfo;
    private View btnIncrease;
    private View imgBack;

    private String pan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_operation);

        btnBuy       = findViewById(R.id.btnBuy);
        btnBalance   = findViewById(R.id.btnBalance);
        btnStatement = findViewById(R.id.btnStatement);
        btnEditInfo  = findViewById(R.id.btnEditInfo);
        btnIncrease  = findViewById(R.id.btnIncrease);
        imgBack      = findViewById(R.id.imgHeaderNext);

        // گرفتن PAN از صفحه قبلی
        pan = getIntent().getStringExtra("PAN");

        // -----------------------------
        // خرید
        // -----------------------------
        btnBuy.setOnClickListener(v -> {
            Intent i = new Intent(SelectOperationActivity.this, AmountActivity.class);
            i.putExtra("mode", "buy");
            i.putExtra("PAN", pan);
            startActivity(i);
        });

        // -----------------------------
        // موجودی
        // -----------------------------
        btnBalance.setOnClickListener(v -> {
            Intent i = new Intent(SelectOperationActivity.this, CustomerPinActivity.class);
            i.putExtra("mode", "balance");
            i.putExtra("pan", pan);
            startActivity(i);
        });

        // -----------------------------
        // صورت‌حساب (بعداً کامل می‌شود)
        // -----------------------------
        btnStatement.setOnClickListener(v -> {
            // TODO
            // Intent i = new Intent(SelectOperationActivity.this, StatementActivity.class);
            // i.putExtra("PAN", pan);
            // startActivity(i);
        });

        // -----------------------------
        // ویرایش اطلاعات مشتری
        // -----------------------------
        btnEditInfo.setOnClickListener(v -> {
            Intent i = new Intent(SelectOperationActivity.this, EditCustomerActivity.class);
            i.putExtra("PAN", pan);
            startActivity(i);
        });

        // -----------------------------
        // افزایش موجودی (شارژ حساب)
        // -----------------------------
        btnIncrease.setOnClickListener(v -> {
            Intent i = new Intent(SelectOperationActivity.this, AmountActivity.class);
            i.putExtra("mode", "charge");
            i.putExtra("PAN", pan);
            startActivity(i);
        });

        // -----------------------------
        // برگشت
        // -----------------------------
        imgBack.setOnClickListener(v -> NavigationHelper.goToWelcome(SelectOperationActivity.this));
    }

    @Override
    public void onBackPressed() {
        NavigationHelper.goToWelcome(this);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SelectOperationActivity.this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }
}
