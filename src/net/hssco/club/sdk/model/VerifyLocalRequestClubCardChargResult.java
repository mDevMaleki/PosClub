package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO for /api/psp/club/charge/verify.
 */
public class VerifyLocalRequestClubCardChargResult {

    @SerializedName("ResponsStatus")
    private String responsStatus;

    public VerifyLocalRequestClubCardChargResult() {
    }

    public String getResponsStatus() {
        return responsStatus;
    }

    public void setResponsStatus(String responsStatus) {
        this.responsStatus = responsStatus;
    }
}
