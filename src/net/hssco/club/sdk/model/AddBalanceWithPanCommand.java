package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for /api/psp/addBalance.
 */
public class AddBalanceWithPanCommand {

    @SerializedName("pan")
    private String pan;

    @SerializedName("amount")
    private long amount;

    public AddBalanceWithPanCommand(String pan, long amount) {
        this.pan = pan;
        this.amount = amount;
    }

    public String getPan() {
        return pan;
    }

    public long getAmount() {
        return amount;
    }
}
