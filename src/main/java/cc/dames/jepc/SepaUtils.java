package cc.dames.jepc;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static boolean validSCOR(String SCOR) {
        if (strEmpty(SCOR)) {
            return true;
        }
        return SCOR.startsWith("RF") && SCOR.length() <= 25;
    }
}
