package com.ernoxin.sepjavasdk.callback;

public record SepCallback(
        SepCallbackStatus status,
        Integer statusCode,
        String token,
        String resNum,
        String refNum,
        String traceNo,
        String terminalId,
        String mid,
        String rrn,
        Long amount,
        Long wage,
        Long affectiveAmount,
        String securePan,
        String hashedCardNumber
) {
    public boolean isOk() {
        return status != null && status.isOk();
    }
}
