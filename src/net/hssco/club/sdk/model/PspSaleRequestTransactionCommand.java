package net.hssco.club.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request DTO for /api/android/psp/sale.
 */
public class PspSaleRequestTransactionCommand {

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

    @SerializedName("Amount")
    private long amount;

    @SerializedName("RefrenceNumber")
    private String refrenceNumber;

    @SerializedName("TerminalID")
    private String terminalId;

    @SerializedName("Stan")
    private String stan;

    @SerializedName("Pan")
    private String pan;

    @SerializedName("CreditValidation")
    private String creditValidation;

    @SerializedName("Pin")
    private String pin;

    @SerializedName("DeviceTypeName")
    private String deviceTypeName;

    @SerializedName("SecondPass")
    private String secondPass;

    @SerializedName("Cvv2")
    private String cvv2;

    @SerializedName("Description")
    private String description;

    @SerializedName("RequestMessage")
    private String requestMessage;

    public PspSaleRequestTransactionCommand() {
    }

    public PspSaleRequestTransactionCommand(long requestId, int pspid, String status, String messageType, String tranDate,
                                            String tranTime, long amount, String refrenceNumber, String terminalId,
                                            String stan, String pan, String creditValidation, String pin,
                                            String deviceTypeName, String secondPass, String cvv2,
                                            String description, String requestMessage) {
        this.requestId = requestId;
        this.pspid = pspid;
        this.status = status;
        this.messageType = messageType;
        this.tranDate = tranDate;
        this.tranTime = tranTime;
        this.amount = amount;
        this.refrenceNumber = refrenceNumber;
        this.terminalId = terminalId;
        this.stan = stan;
        this.pan = pan;
        this.creditValidation = creditValidation;
        this.pin = pin;
        this.deviceTypeName = deviceTypeName;
        this.secondPass = secondPass;
        this.cvv2 = cvv2;
        this.description = description;
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getRefrenceNumber() {
        return refrenceNumber;
    }

    public void setRefrenceNumber(String refrenceNumber) {
        this.refrenceNumber = refrenceNumber;
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

    public String getCreditValidation() {
        return creditValidation;
    }

    public void setCreditValidation(String creditValidation) {
        this.creditValidation = creditValidation;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getSecondPass() {
        return secondPass;
    }

    public void setSecondPass(String secondPass) {
        this.secondPass = secondPass;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
}
