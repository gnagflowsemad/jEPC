package cc.dames.jepc;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import static cc.dames.jepc.SepaUtils.*;

/**
 * SEPA Credit Transfer
 * SCT transaction data
 * QR code error level M (15% of code words can be restored)
 * <a href="https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation">Guidelines to Enable Data Capture for the Initiation of a SEPA Credit Transfer</a>
 * @author gnagflowsemad
 * @version 1.0
 */
public final class Epc {

    private Epc() {
    }

    public static class Builder {

        private LineFeed lf = LineFeed.LF;

        // row 1
        private static final String BCD = "BCD";

        // row 2
        private Version version = Version.V002;

        // row 3
        private int characterEncoding = 1; // UTF-8

        // row 4
        private static final String SCT = "SCT"; // SEPA Credit Transfer

        // row 5
        private String bic; // empty value is allowed if version is 002

        // row 6
        private String issuer; // Beneficiary, 70 characters max, "Kontoinhaber", "Rechnungssteller"

        // row 7
        private String iban;

        // row 8
        private BigDecimal transferAmount = null;

        // row 9
        private SepaPurpose sepaPurpose; // optional

        // row 10
        private String scor; // Structured Creditor Reference, ISO 11649, max. 25 characters

        // row 11
        private String intendedUse; // "Verwendungszweck"

        // row 12
        private String message; // max. 70 characters

        private boolean umlauts = false;

        /**
         * @param value line feed to use for whole document
         * @return Epc object
         * @since 0.0.1
         */
        public Builder withLineFeed(LineFeed value) {
            this.lf = value;
            return this;
        }

        /**
         * version, mandatory
         * @param value 001 or 002, BIC is mandatory if 001 is used
         * @return Epc object
         */
        public Builder withVersion(Version value) {
            this.version = value;
            return this;
        }

        /**
         * Character set, 1 default
         * <ul>
         * <li>UTF-8
         * <li>ISO 8859-1
         * <li>ISO 8859-2
         * <li>ISO 8859-4
         * <li>ISO 8859-5
         * <li>ISO 8859-7
         * <li>ISO 8859-10
         * <li>ISO 8859-15
         * </ul>
         * @param value character encoding, 1-8
         * @return Epc object
         */
        public Builder withCharacterEncoding(int value) {
            this.characterEncoding = value;
            return this;
        }

        /**
         * allow german umlauts, does not have an effect how credit institute treatment
         * @param value true if umlauts are allowed
         * @return Epc object
         */
        public Builder withUmlauts(boolean value) {
            this.umlauts = value;
            return this;
        }

        /**
         * mandatory for non-EEA countries
         * The BIC will continue to be mandatory for SEPA
         * payment transactions involving SCT scheme
         * participants from non-EEA countries.
         * @param value BIC, only mandatory if version is 001
         * @return Epc object
         */
        public Builder withBIC(String value) {
            this.bic = value.trim();
            return this;
        }

        /**
         * issuer of transfer, mandatory
         * @param value The name of the Beneficiary
         * @return Epc object
         */
        public Builder withIssuer(String value) {
            this.issuer = value.trim();
            return this;
        }

        /**
         * The IBAN of the account of the Beneficiary, mandatory
         * @param value IBAN
         * @return Epc object
         */
        public Builder withIBAN(String value) {
            this.iban = value == null ? "" : value.replace(" ", "").trim();
            return this;
        }

        /**
         * Amount of the SEPA Credit Transfer in euro, mandatory
         * Amount must be larger than or equal to 0.01, and
         * cannot be larger than 999999999.99
         * @param value amount
         * @return Epc object
         */
        public Builder withTransferAmount(BigDecimal value) {
            this.transferAmount = value;
            return this;
        }

        /**
         * Amount of the SEPA Credit Transfer in euro, mandatory
         * Amount must be larger than or equal to 0.01, and
         * cannot be larger than 999999999.99
         * @param value amount
         * @return Epc object
         */
        public Builder withTransferAmount(String value) {
            this.transferAmount = new BigDecimal(strEmpty(value) ? "" : value.replace(",", ".").trim());
            return this;
        }

        /**
         * Purpose of the SEPA Credit Transfer, optional
         * @param value purpose
         * @return Epc object
         */
        public Builder withSepaPurpose(SepaPurpose value) {
            this.sepaPurpose = value;
            return this;
        }

        /**
         * The Remittance Information (Unstructured), "Verwendungszweck", optional
         * @param value intended use
         * @return Epc object
         */
        public Builder withIntendedUse(String value) {
            this.intendedUse = value.trim();
            return this;
        }

        /**
         * The Remittance Information (Structured), optional
         * ISO 11649
         * @param value Structured Creditor Reference, allows receiver to assign payment
         * <a href="https://en.wikipedia.org/wiki/Creditor_Reference">Creditor Reference</a>
         * @return Epc object
         */
        public Builder withScor(String value) {
            this.scor = value.trim();
            return this;
        }

        /**
         * Beneficiary to Originator information, optional
         * @param value additional message
         * @return Epc object
         */
        public Builder withMessage(String value) {
            this.message = value.trim();
            return this;
        }

        public String build() {

            final String CURRENCY = "EUR";

            if (Version.V001 == version && strEmpty(bic)) {
                throw new EpcException("BIC can not be empty if version is " + Version.V001.getCode());
            }

            if (strEmpty(iban)) {
                throw new EpcException("IBAN can not be empty");
            }

            if (transferAmount == null) {
                throw new EpcException("transfer amount can not be empty");
            }

            if (strNotEmpty(scor) && strNotEmpty(intendedUse)) {
                throw new EpcException("either SCOR or intended use can be set");
            }

            final String lineFeedCode = lf.getCode();

            StringBuilder sb = new StringBuilder(BCD);
            sb.append(lineFeedCode);
            sb.append(version.getCode());
            sb.append(lineFeedCode);
            sb.append(checkCharacterEncoding(characterEncoding));
            sb.append(lineFeedCode);
            sb.append(SCT);
            sb.append(lineFeedCode);
            sb.append(checkBIC(bic));
            sb.append(lineFeedCode);
            sb.append(checkIssuer(issuer));
            sb.append(lineFeedCode);
            sb.append(checkIBAN(iban));
            sb.append(lineFeedCode);
            sb.append(CURRENCY);
            sb.append(checkTransferAmount(transferAmount));
            sb.append(lineFeedCode);
            sb.append(checkSepaPurpose(sepaPurpose));
            sb.append(lineFeedCode);
            sb.append(checkSCOR(scor));
            sb.append(lineFeedCode);
            sb.append(checkIntendedUse(intendedUse));
            sb.append(lineFeedCode);
            sb.append(checkMessage(message));

            return sb.toString();
        }

        private int checkCharacterEncoding(int characterEncoding) {
            if (characterEncoding < 1 || characterEncoding > 8) {
                throw new EpcException("character encoding must be between 1 and 8");
            }
            return characterEncoding;
        }

        private String checkBIC(String value) throws EpcException {
            if (strEmpty(value)) {
                return "";
            }
            if (value.length() > 11) {
                throw new EpcException("BIC exceed allowed length, max. 11");
            }
            if (!BIC_REGEX_PATTERN.matcher(value).matches()) {
                throw new EpcException("BIC contains invalid character(s)");
            }
            return value;
        }

        private String checkIssuer(String value) {
            if (strEmpty(value)) {
                return "";
            }
            if (value.length() > 70) {
                throw new EpcException("issuer exceed allowed length, max. 70");
            }
            Matcher matcher = umlauts ? SEPA_TEXT_UMLAUTS_PATTERN.matcher(value) : SEPA_TEXT_PATTERN.matcher(value);
            if (!matcher.matches()) {
                throw new EpcException("issuer contains invalid character(s)");
            }
            return value;
        }

        private String checkIBAN(String value) {
            if (strEmpty(value)) {
                throw new EpcException("IBAN is mandatory");
            }
            if (value.length() > 34) {
                throw new EpcException("IBAN exceed allowed length, max. 34");
            }
            Matcher matcher = IBAN_PATTERN.matcher(iban);
            if (!matcher.matches()) {
                throw new EpcException("IBAN has invalid format");
            }
            return iban;
        }

        private BigDecimal checkTransferAmount(BigDecimal value) {
            if (exceedAmount(value)) {
                throw new EpcException("transfer amount is out of valid range, (0.01 - 999999999.99)");
            }
            return bankersRounding(value);
        }

        private String checkSepaPurpose(SepaPurpose value) {
            return value == null ? "" : value.name();
        }

        private String checkSCOR(String value) {
            if (strEmpty(value)) {
                return "";
            }
            String sanitized = value.replace(" ", "");
            if (!validateSCOR(sanitized)) {
                throw new EpcException("SCOR has invalid format or checksum");
            }
            return sanitized;
        }

        private String checkIntendedUse(String value) {
            if (strEmpty(value)) {
                return "";
            }
            if (value.length() > 140) {
                throw new EpcException("intended use contains to many character(s), max. 140");
            }
            Matcher matcher = umlauts ? SEPA_TEXT_UMLAUTS_PATTERN.matcher(value) : SEPA_TEXT_PATTERN.matcher(value);
            if (!matcher.matches()) {
                throw new EpcException("intended use contains invalid character(s)");
            }
            return value;
        }

        private String checkMessage(String value) {
            if (strEmpty(value)) {
                return "";
            }
            if (value.length() > 70) {
                throw new EpcException("message contains to many character(s), max. 70");
            }
            Matcher matcher = umlauts ? SEPA_TEXT_UMLAUTS_PATTERN.matcher(value) : SEPA_TEXT_PATTERN.matcher(value);
            if (!matcher.matches()) {
                throw new EpcException("message contains invalid character(s)");
            }
            return value;
        }

    }

}
