package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for /api/android/psp/balance.
 */
public class GetBalanceRequestTransactionCommand {

    @SerializedName("RequestID")
    private long requestId;

    @SerializedName("PSPID")
    private int pspid;

    @SerializedName("Status")
    private String status;

    @SerializedName("MessageType")
    private String messageType;

    @SerializedName("TranDate")
    private String tranDate;

    @SerializedName("TranTime")
    private String tranTime;

    @SerializedName("TerminalID")
    private String terminalId;

    @SerializedName("Stan")
    private String stan;

    @SerializedName("Pan")
    private String pan;

    @SerializedName("Pin")
    private String pin;

    @SerializedName("RequestMessage")
    private String requestMessage;

    public GetBalanceRequestTransactionCommand() {
    }

    public GetBalanceRequestTransactionCommand(long requestId, int pspid, String status, String messageType,
                                               String tranDate, String tranTime, String terminalId, String stan,
                                               String pan, String pin, String requestMessage) {
        this.requestId = requestId;
        this.pspid = pspid;
        this.status = status;
        this.messageType = messageType;
        this.tranDate = tranDate;
        this.tranTime = tranTime;
        this.terminalId = terminalId;
        this.stan = stan;
        this.pan = pan;
        this.pin = pin;
        this.requestMessage = requestMessage;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranTime() {
        return tranTime;
    }

    public void setTranTime(String tranTime) {
        this.tranTime = tranTime;
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

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
}
