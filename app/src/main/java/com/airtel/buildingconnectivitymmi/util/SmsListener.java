package com.airtel.buildingconnectivitymmi.util;

//Helping in listening to sms received for auto reading OTP
public interface SmsListener {

    public void messageReceived(String messageText);
}
