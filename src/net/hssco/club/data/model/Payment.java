package net.hssco.club.data.model;

public class Payment {

    private String applicationId;
    private Integer result;
    private String message;
    private String totalAmount;
    private String purchaseId;
    private TransactionTypeIntent transactionType;
    private String rrn;
    private String stan;
    private String dateTime;
    private String bankName;
    private String cardNumber;
    private String encryptedCardNumber;
    private String terminalId;
    private String merchantName;

    // --- Constructor ---
    public Payment() {
        this.applicationId = "";
        this.result = 1;
        this.message = "";
        this.totalAmount = "";
        this.purchaseId = "";
        this.transactionType = null;
        this.rrn = "";
        this.stan = "";
        this.dateTime = "";
        this.bankName = "";
        this.cardNumber = "";
        this.encryptedCardNumber = "";
        this.terminalId = "";
        this.merchantName = "";
    }

    // --- Getters and Setters ---
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getResult() { return result; }
    public void setResult(Integer result) { this.result = result; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTotalAmount() { return totalAmount; }
    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }

    public String getPurchaseId() { return purchaseId; }
    public void setPurchaseId(String purchaseId) { this.purchaseId = purchaseId; }

    public TransactionTypeIntent getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionTypeIntent transactionType) { this.transactionType = transactionType; }

    public String getRrn() { return rrn; }
    public void setRrn(String rrn) { this.rrn = rrn; }

    public String getStan() { return stan; }
    public void setStan(String stan) { this.stan = stan; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getEncryptedCardNumber() { return encryptedCardNumber; }
    public void setEncryptedCardNumber(String encryptedCardNumber) { this.encryptedCardNumber = encryptedCardNumber; }

    public String getTerminalId() { return terminalId; }
    public void setTerminalId(String terminalId) { this.terminalId = terminalId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
}



