package com.ernoxin.sepjavasdk.support;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SepEndpoints {
    public static String token() {
        return "/onlinepg/onlinepg";
    }

    public static String paymentPage() {
        return "/OnlinePG/OnlinePG";
    }

    public static String sendToken() {
        return "/OnlinePG/SendToken";
    }

    public static String verify() {
        return "/verifyTxnRandomSessionkey/ipg/VerifyTransaction";
    }

    public static String reverse() {
        return "/verifyTxnRandomSessionkey/ipg/ReverseTransaction";
    }
}
