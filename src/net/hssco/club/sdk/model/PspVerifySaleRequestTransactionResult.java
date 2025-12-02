package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for /api/psp/sale/verify.
 */
public class PspVerifySaleRequestTransactionResult {

    @SerializedName("ResponsStatus")
    private String responsStatus;

    public PspVerifySaleRequestTransactionResult() {
    }

    public String getResponsStatus() {
        return responsStatus;
    }

    public void setResponsStatus(String responsStatus) {
        this.responsStatus = responsStatus;
    }
}
