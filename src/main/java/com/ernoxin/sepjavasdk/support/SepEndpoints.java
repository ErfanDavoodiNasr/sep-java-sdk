package com.ernoxin.sepjavasdk.support;

import lombok.experimental.UtilityClass;

/**
 * SEP endpoint path constants exposed as methods.
 */
@UtilityClass
public class SepEndpoints {
    /**
     * Token request endpoint path.
     *
     * @return token endpoint path
     */
    public static String token() {
        return "/onlinepg/onlinepg";
    }

    /**
     * Payment page endpoint path.
     *
     * @return payment page path
     */
    public static String paymentPage() {
        return "/OnlinePG/OnlinePG";
    }

    /**
     * Send-token endpoint path used for customer redirect.
     *
     * @return send-token endpoint path
     */
    public static String sendToken() {
        return "/OnlinePG/SendToken";
    }

    /**
     * Transaction verification endpoint path.
     *
     * @return verification endpoint path
     */
    public static String verify() {
        return "/verifyTxnRandomSessionkey/ipg/VerifyTransaction";
    }

    /**
     * Transaction reverse endpoint path.
     *
     * @return reverse endpoint path
     */
    public static String reverse() {
        return "/verifyTxnRandomSessionkey/ipg/ReverseTransaction";
    }
}
