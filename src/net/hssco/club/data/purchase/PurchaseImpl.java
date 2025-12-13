package net.hssco.club.data.purchase;


import android.content.ComponentName;
import android.content.Intent;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hssco.club.data.model.Payment;

public final class PurchaseImpl implements Purchase {

    private static final PurchaseImpl INSTANCE = new PurchaseImpl();
    public static PurchaseImpl getInstance() { return INSTANCE; }

    private static final String PACKAGE_NAME = "ir.omidpayment.fourmainactions";
    private static final String PAYMENT_DATA = "PaymentData";
    private static final String TARGET_ACTIVITY = "ir.omidpayment.fourmainactions.MainActivity";

    private final Gson gson;

    private PurchaseImpl() {
        gson = new GsonBuilder().create();
    }

    // نسخه مخصوص onActivityResult
    public Payment receiveResult(Intent data) {
        if (data == null) return null;

        String jsonString = data.getStringExtra(PAYMENT_DATA);
        if (jsonString == null) return null;

        try {
            return gson.fromJson(jsonString, Payment.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Intent createEmptyIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PACKAGE_NAME, TARGET_ACTIVITY));
        return intent;
    }
    @Override
    public Intent createIntent(Payment paymentData) {
        String data = gson.toJson(paymentData);

        Intent intent = new Intent();
        intent.putExtra(PAYMENT_DATA, data);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setComponent(new ComponentName(PACKAGE_NAME, TARGET_ACTIVITY));

        return intent;
    }
}
