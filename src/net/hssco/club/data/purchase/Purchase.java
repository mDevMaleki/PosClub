package net.hssco.club.data.purchase;

import android.content.Intent;

import net.hssco.club.data.model.Payment;

public interface Purchase {


    Payment receiveResult(Intent data);


    Intent createIntent(Payment paymentData);
}
