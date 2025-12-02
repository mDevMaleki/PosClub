package net.hssco.club;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class SelectOperationActivity extends AppCompatActivity {

    private View btnBuy;
    private View btnBalance;
    private View btnStatement;
    private View btnEditInfo;
    private View btnIncrease;
    private View imgBack;

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

        final String pan = getIntent().getStringExtra("PAN");

        // خرید
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectOperationActivity.this, AmountActivity.class);
                i.putExtra("mode", "buy");
                startActivity(i);
            }
        });

        // موجودی
        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectOperationActivity.this, CustomerPinActivity.class);
                i.putExtra("mode", "balance");
                i.putExtra("pan", pan);
                startActivity(i);
            }
        });

        // صورت‌حساب (فعلاً خالی یا TODO)
        btnStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: بعداً Activity گزارش/صورت‌حساب را اینجا صدا بزن
                // Intent i = new Intent(SelectOperationActivity.this, StatementActivity.class);
                // i.putExtra("pan", pan);
                // startActivity(i);
            }
        });

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectOperationActivity.this, EditCustomerActivity.class);
                // اگر pan یا شناسه مشتری داری، اینجا بفرست
                // i.putExtra("PAN", pan);
                startActivity(i);
            }
        });


        // افزایش موجودی (فعلاً خالی)
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectOperationActivity.this, AmountActivity.class);
                i.putExtra("mode", "charge");
                startActivity(i);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectOperationActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
