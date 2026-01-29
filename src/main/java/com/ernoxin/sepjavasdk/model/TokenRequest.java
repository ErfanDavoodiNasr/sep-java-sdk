package com.ernoxin.sepjavasdk.model;

import java.net.URI;
import java.util.List;

public record TokenRequest(
        long amount,
        String resNum,
        URI redirectUrl,
        String cellNumber,
        Long wage,
        Integer tokenExpiryInMin,
        List<String> hashedCardNumbers,
        Boolean getMethod,
        String resNum1,
        String resNum2,
        String resNum3,
        String resNum4,
        SepTranType tranType,
        List<SettlementIbanInfo> settlementIbanInfo
) {
    public static Builder builder(long amount, String resNum) {
        return new Builder(amount, resNum);
    }

    public static final class Builder {
        private final long amount;
        private final String resNum;
        private URI redirectUrl;
        private String cellNumber;
        private Long wage;
        private Integer tokenExpiryInMin;
        private List<String> hashedCardNumbers;
        private Boolean getMethod;
        private String resNum1;
        private String resNum2;
        private String resNum3;
        private String resNum4;
        private SepTranType tranType;
        private List<SettlementIbanInfo> settlementIbanInfo;

        private Builder(long amount, String resNum) {
            this.amount = amount;
            this.resNum = resNum;
        }

        public Builder redirectUrl(URI redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public Builder cellNumber(String cellNumber) {
            this.cellNumber = cellNumber;
            return this;
        }

        public Builder wage(Long wage) {
            this.wage = wage;
            return this;
        }

        public Builder tokenExpiryInMin(Integer tokenExpiryInMin) {
            this.tokenExpiryInMin = tokenExpiryInMin;
            return this;
        }

        public Builder hashedCardNumbers(List<String> hashedCardNumbers) {
            this.hashedCardNumbers = hashedCardNumbers;
            return this;
        }

        public Builder getMethod(Boolean getMethod) {
            this.getMethod = getMethod;
            return this;
        }

        public Builder resNum1(String resNum1) {
            this.resNum1 = resNum1;
            return this;
        }

        public Builder resNum2(String resNum2) {
            this.resNum2 = resNum2;
            return this;
        }

        public Builder resNum3(String resNum3) {
            this.resNum3 = resNum3;
            return this;
        }

        public Builder resNum4(String resNum4) {
            this.resNum4 = resNum4;
            return this;
        }

        public Builder tranType(SepTranType tranType) {
            this.tranType = tranType;
            return this;
        }

        public Builder settlementIbanInfo(List<SettlementIbanInfo> settlementIbanInfo) {
            this.settlementIbanInfo = settlementIbanInfo;
            return this;
        }

        public TokenRequest build() {
            return new TokenRequest(
                    amount,
                    resNum,
                    redirectUrl,
                    cellNumber,
                    wage,
                    tokenExpiryInMin,
                    hashedCardNumbers,
                    getMethod,
                    resNum1,
                    resNum2,
                    resNum3,
                    resNum4,
                    tranType,
                    settlementIbanInfo
            );
        }
    }
}
