package com.ernoxin.sepjavasdk.model;

import java.net.URI;
import java.util.List;

/**
 * Request payload for SEP token generation.
 *
 * <p>Amounts are sent as integer gateway units (typically IRR for SEP terminals). Convert from IRT
 * if your domain stores toman values.
 *
 * @param amount transaction amount; must be positive
 * @param resNum merchant unique order/reference id; non-blank and limited by configuration
 * @param redirectUrl optional callback URL override; when {@code null}, SDK uses configured
 * callback URL
 * @param cellNumber optional customer mobile number
 * @param wage optional wage amount; when provided must be non-negative
 * @param tokenExpiryInMin optional token expiry in minutes; normalized to configured min/max range
 * @param hashedCardNumbers optional hashed card constraints; if provided must be non-empty
 * @param getMethod optional callback method hint; {@code true} means callback via GET
 * @param resNum1 optional additional reference field
 * @param resNum2 optional additional reference field
 * @param resNum3 optional additional reference field
 * @param resNum4 optional additional reference field
 * @param tranType optional transaction type
 * @param settlementIbanInfo optional settlement items; required for government transactions
 */
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
    /**
     * Creates a builder with required token request fields.
     *
     * @param amount transaction amount
     * @param resNum merchant reference id
     * @return token request builder
     */
    public static Builder builder(long amount, String resNum) {
        return new Builder(amount, resNum);
    }

    /**
     * Builder for {@link TokenRequest}.
     */
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

        /**
         * Creates builder with required fields.
         *
         * @param amount transaction amount
         * @param resNum merchant reference id
         */
        private Builder(long amount, String resNum) {
            this.amount = amount;
            this.resNum = resNum;
        }

        /**
         * Sets request-specific redirect URL.
         *
         * @param redirectUrl callback URL override
         * @return this builder
         */
        public Builder redirectUrl(URI redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        /**
         * Sets customer mobile number.
         *
         * @param cellNumber mobile number
         * @return this builder
         */
        public Builder cellNumber(String cellNumber) {
            this.cellNumber = cellNumber;
            return this;
        }

        /**
         * Sets wage amount.
         *
         * @param wage wage amount
         * @return this builder
         */
        public Builder wage(Long wage) {
            this.wage = wage;
            return this;
        }

        /**
         * Sets preferred token expiry in minutes.
         *
         * @param tokenExpiryInMin expiry in minutes
         * @return this builder
         */
        public Builder tokenExpiryInMin(Integer tokenExpiryInMin) {
            this.tokenExpiryInMin = tokenExpiryInMin;
            return this;
        }

        /**
         * Restricts allowed payment cards by hashed PAN values.
         *
         * @param hashedCardNumbers hashed card list
         * @return this builder
         */
        public Builder hashedCardNumbers(List<String> hashedCardNumbers) {
            this.hashedCardNumbers = hashedCardNumbers;
            return this;
        }

        /**
         * Sets callback method preference.
         *
         * @param getMethod {@code true} for GET callback
         * @return this builder
         */
        public Builder getMethod(Boolean getMethod) {
            this.getMethod = getMethod;
            return this;
        }

        /**
         * Sets optional reporting field {@code ResNum1}.
         *
         * @param resNum1 additional reference value
         * @return this builder
         */
        public Builder resNum1(String resNum1) {
            this.resNum1 = resNum1;
            return this;
        }

        /**
         * Sets optional reporting field {@code ResNum2}.
         *
         * @param resNum2 additional reference value
         * @return this builder
         */
        public Builder resNum2(String resNum2) {
            this.resNum2 = resNum2;
            return this;
        }

        /**
         * Sets optional reporting field {@code ResNum3}.
         *
         * @param resNum3 additional reference value
         * @return this builder
         */
        public Builder resNum3(String resNum3) {
            this.resNum3 = resNum3;
            return this;
        }

        /**
         * Sets optional reporting field {@code ResNum4}.
         *
         * @param resNum4 additional reference value
         * @return this builder
         */
        public Builder resNum4(String resNum4) {
            this.resNum4 = resNum4;
            return this;
        }

        /**
         * Sets transaction type.
         *
         * @param tranType transaction type flag
         * @return this builder
         */
        public Builder tranType(SepTranType tranType) {
            this.tranType = tranType;
            return this;
        }

        /**
         * Sets settlement split items.
         *
         * @param settlementIbanInfo settlement entries
         * @return this builder
         */
        public Builder settlementIbanInfo(List<SettlementIbanInfo> settlementIbanInfo) {
            this.settlementIbanInfo = settlementIbanInfo;
            return this;
        }

        /**
         * Builds immutable request object.
         *
         * <p>Validation is performed later by {@code SepClient.requestToken(...)}.
         *
         * @return token request
         */
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
