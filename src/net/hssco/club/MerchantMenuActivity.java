package net.hssco.club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MerchantMenuActivity extends Activity {

    private Button btnPrintReceipt;
    private Button btnLastTransaction;
    private Button btnTransactionsReport;
    private Button btnChangePinMerchant;
    private Button btnSaveMerchant;
    private Button btnCancelMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_menu);

        btnPrintReceipt = (Button) findViewById(R.id.btnPrintReceipt);
        btnLastTransaction = (Button) findViewById(R.id.btnLastTransaction);
        btnTransactionsReport = (Button) findViewById(R.id.btnTransactionsReport);
        btnChangePinMerchant = (Button) findViewById(R.id.btnChangePinMerchant);
        btnSaveMerchant = (Button) findViewById(R.id.btnSaveMerchant);
        btnCancelMerchant = (Button) findViewById(R.id.btnCancelMerchant);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnPrintReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: پرینت رسید آخر
                Toast.makeText(MerchantMenuActivity.this,
                        "پرینت رسید (هنوز پیاده‌سازی نشده)",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnLastTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: نمایش تراکنش آخر
                Toast.makeText(MerchantMenuActivity.this,
                        "نمایش تراکنش آخر",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnTransactionsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: گزارش تراکنش‌ها
                Toast.makeText(MerchantMenuActivity.this,
                        "گزارش تراکنش‌ها",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePinMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // همان صفحه تغییر رمز سرپرست
                Intent i = new Intent(MerchantMenuActivity.this, ChangeAdminPinActivity.class);
                startActivity(i);
            }
        });

        btnChangePinMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MerchantMenuActivity.this, ChangeAdminPinActivity.class);
                i.putExtra("TARGET_MENU", "MERCHANT");
                startActivity(i);
            }
        });


        btnCancelMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
