package cc.dames.jepc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SepaUtils {

    private SepaUtils() {
    }

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999.99");
    private static final String IBAN_REGEX = "^(?:((?:IT|SM)\\d{2}[A-Z]{1}\\d{22})|(NL\\d{2}[A-Z]{4}\\d{10})|(LV\\d{2}[A-Z]{4}\\d{13})|((?:BG|GB|IE)\\d{2}[A-Z]{4}\\d{14})|(GI\\d{2}[A-Z]{4}\\d{15})|(RO\\d{2}[A-Z]{4}\\d{16})|(MT\\d{2}[A-Z]{4}\\d{23})|(NO\\d{13})|((?:DK|FI|FO)\\d{16})|((?:SI)\\d{17})|((?:AT|EE|LU|LT)\\d{18})|((?:HR|LI|CH)\\d{19})|((?:DE)\\d{20})|((?:CZ|ES|SK|SE)\\d{22})|(PT\\d{23})|((?:IS)\\d{24})|((?:BE)\\d{14})|((?:FR|MC|GR)\\d{25})|((?:PL|HU|CY)\\d{26}))$";

    public static final Pattern IBAN_PATTERN = Pattern.compile(IBAN_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private final static String SEPA_TEXT = "[a-zA-Z0-9\\/\\-?:().,+'& ]+";

    public static final Pattern SEPA_TEXT_PATTERN = Pattern.compile(SEPA_TEXT);

    private static final String SEPA_TEXT_UMLAUTS = "[a-zA-Z0-9\\/\\-?:().,+&' öäüÄÖÜß]+";

    public static final Pattern SEPA_TEXT_UMLAUTS_PATTERN = Pattern.compile(SEPA_TEXT_UMLAUTS, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static final String BIC_REGEX = "([a-zA-Z]{4})([a-zA-Z]{2})(([2-9a-zA-Z]{1})([0-9a-np-zA-NP-Z]{1}))((([0-9a-wy-zA-WY-Z]{1})([0-9a-zA-Z]{2}))|([xX]{3})|)";

    private static final String SCOR_PREFIX = "RF";

    public static final Pattern BIC_REGEX_PATTERN = Pattern.compile(BIC_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    public static boolean strNotEmpty(final String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean strEmpty(final String str) {
        return !strNotEmpty(str);
    }

    public static boolean exceedAmount(BigDecimal amount) {
        if (amount == null || BigDecimal.ZERO.equals(amount)) {
            return true;
        }
        return amount.compareTo(MAX_AMOUNT) > 0 || amount.signum() == -1;
    }

    public static BigDecimal bankersRounding(BigDecimal value) {
        value = value != null ? value : new BigDecimal(0);
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static String createSCOR(String reference) {
        if (strEmpty(reference)) {
            return "";
        }
        return SCOR_PREFIX + calculateCheckSum(reference) + reference;
    }

    /**
     * <a href="https://www.mobilefish.com/services/creditor_reference/creditor_reference.php">...</a>
     * @param scor value to validate
     * @return true if scor is valid creditor reference
     */
    public static boolean validateSCOR(String scor) {
        if (strEmpty(scor)) {
            return false;
        }
        if (!scor.startsWith(SCOR_PREFIX)) {
            return false;
        }

        scor = scor.replace(" ", "");
        if (scor.length() > 25) {
            return false;
        }
        if (!scor.matches("RF[0-9]{2}[0-9A-Z]+")) {
            return false;
        }

        String prefix = scor.substring(0, 4);
        String reference = scor.replace(prefix, "");

        BigInteger bi = new BigInteger(substituteCharWithNumber(reference + prefix));
        return bi.mod(new BigInteger("97")).equals(BigInteger.ONE);
    }

    public static boolean validateIBAN(String iban) {
        if (strEmpty(iban)) {
            return false;
        }
        iban = iban.replace(" ", "");
        Matcher matcher = IBAN_PATTERN.matcher(iban);
        if (!matcher.matches()) {
            return false;
        }
        String prefix = iban.substring(0, 4);
        String reference = iban.replace(prefix, "");
        BigInteger bi = new BigInteger(substituteCharWithNumber(reference + prefix));
        return bi.mod(new BigInteger("97")).equals(BigInteger.ONE);
    }

    private static int calculateCheckSum(String reference) {
        if (strEmpty(reference)) {
            return 0;
        }
        return
                new BigInteger("98")
                .subtract(new BigInteger(substituteCharWithNumber(reference + SCOR_PREFIX + "00"))
                .mod(new BigInteger("97")))
                .intValue();
    }

    private static String substituteCharWithNumber(String value) {
        if (strEmpty(value)) {
            return "";
        }
        CharacterIterator it = new StringCharacterIterator(value);
        StringBuilder decoded = new StringBuilder();
        while (it.current() != CharacterIterator.DONE) {
            if (isLetter(it.current())) {
                decoded.append(CharDigitTranslation.valueOf(String.valueOf(it.current()).toLowerCase()).getDigit());
            } else {
                decoded.append(it.current());
            }
            it.next();
        }
        return decoded.toString();
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

}
