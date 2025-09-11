package cz.soft4you.smsgate;

public class SmsMessage {
    public String Phone;
    public String Message;

    public SmsMessage(String phone, String message) {
        this.Phone = phone;
        this.Message = message;
    }
}
