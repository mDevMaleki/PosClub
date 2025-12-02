package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for /api/psp/sale/verify.
 */
public class PspVerifySaleRequestTransactionCommand {

    @SerializedName("RequestID")
    private long requestId;

    @SerializedName("PSPID")
    private int pspid;

    @SerializedName("TerminalID")
    private String terminalId;

    @SerializedName("Stan")
    private String stan;

    public PspVerifySaleRequestTransactionCommand() {
    }

    public PspVerifySaleRequestTransactionCommand(long requestId, int pspid, String terminalId, String stan) {
        this.requestId = requestId;
        this.pspid = pspid;
        this.terminalId = terminalId;
        this.stan = stan;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getPspid() {
        return pspid;
    }

    public void setPspid(int pspid) {
        this.pspid = pspid;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }
}
