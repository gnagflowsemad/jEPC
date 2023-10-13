package cc.dames.jepc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cc.dames.jepc.SepaUtils.*;
import static cc.dames.jepc.SepaUtils.validateSCOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SepaUtilsTest {

    @Test
    void sepaTextPatternTest() {
        assertTrue(SEPA_TEXT_PATTERN.matcher("Wikimedia Foerdergesellschaft").matches());
        assertTrue(SEPA_TEXT_PATTERN.matcher("Bitte innerhalb der naechsten 14 Tage ueberweisen").matches());
        assertTrue(SEPA_TEXT_PATTERN.matcher("A&B Events").matches());
        assertFalse(SEPA_TEXT_PATTERN.matcher("Wikimedia Fördergesellschaft").matches());
        assertTrue(SEPA_TEXT_UMLAUTS_PATTERN.matcher("Wikimedia Fördergesellschaft").matches());
        assertTrue(SEPA_TEXT_UMLAUTS_PATTERN.matcher("A&B Events").matches());
    }

    @Test
    void ibanPatternTest() {
        assertFalse(IBAN_PATTERN.matcher("").matches());
        assertTrue(IBAN_PATTERN.matcher("DE19200411330823122700").matches());
        assertTrue(IBAN_PATTERN.matcher("DE89500105179394767432").matches());
        assertTrue(IBAN_PATTERN.matcher("DE44500105173481939824").matches());
        assertTrue(IBAN_PATTERN.matcher("NL79RABO2423554788").matches());
        assertTrue(IBAN_PATTERN.matcher("CH8589144649296413173").matches());
        assertTrue(IBAN_PATTERN.matcher("AT835400037618454391").matches());
    }

    @Test
    void bicPatternTest() {
        assertFalse(BIC_REGEX_PATTERN.matcher("").matches());
        assertTrue(BIC_REGEX_PATTERN.matcher("COBADEHD001").matches());
        assertTrue(BIC_REGEX_PATTERN.matcher("COBADEFF060").matches());
        assertTrue(BIC_REGEX_PATTERN.matcher("GEBABEBB").matches());
        assertTrue(BIC_REGEX_PATTERN.matcher("ZUNOCZPP").matches());
    }

    @Test
    void maxAmountTest() {
        assertFalse(exceedAmount(new BigDecimal("0.01")));
        assertFalse(exceedAmount(new BigDecimal("999999999.99")));
        assertTrue(exceedAmount(new BigDecimal("-0.01")));
        assertTrue(exceedAmount(new BigDecimal("0")));
        assertTrue(exceedAmount(BigDecimal.ZERO));
        assertTrue(exceedAmount(null));
        assertTrue(exceedAmount(new BigDecimal("1000000000")));
    }

    @Test
    void strEmptyTest() {
        assertTrue(strEmpty(""));
        assertFalse(strEmpty("A"));
        assertTrue(strEmpty(null));
    }

    @Test
    void strNotEmptyTest() {
        assertTrue(strNotEmpty(" "));
        assertTrue(strNotEmpty("A"));
        assertFalse(strNotEmpty(""));
    }

    @Test
    void validateSCORTest() {
        assertFalse(validateSCOR(""));
        assertFalse(validateSCOR(null));
        assertFalse(validateSCOR("RF214377"));
        assertFalse(validateSCOR("Rv45G72UUR"));
        assertFalse(validateSCOR("rv45G72UUR"));
        assertTrue(validateSCOR("RF45G72UUR"));
        assertTrue(validateSCOR("RF6518K5"));
        assertFalse(validateSCOR("RF35C4"));
        assertTrue(validateSCOR("RF18 5390 0754 7034"));
        assertTrue(validateSCOR("RF18000000000539007547034"));
        assertTrue(validateSCOR("RF48XNO3G76VUE05CW1CC0FWK"));
        assertTrue(validateSCOR("RF9157QT3D9OD"));
        assertTrue(validateSCOR("RF29Z"));
        assertTrue(validateSCOR("RF89M"));
        assertTrue(validateSCOR("RF13HO6YZPQJ27"));
        assertTrue(validateSCOR("RF29B99"));
        assertTrue(validateSCOR("RF42U0SR08RDVSXQEAUQCQJ0R"));
        assertTrue(validateSCOR("RF2290897867TREKKERTJE"));
        assertTrue(validateSCOR("RF35WOLFGANG"));
        assertTrue(validateSCOR("RF4714508655422864"));
        assertTrue(validateSCOR("RF794723M108"));
    }

    @Test
    void createSCORTest() {
        assertEquals("RF794723M108", createSCOR("4723M108"));
    }

    @Test
    void validateIBANTest() {
        assertFalse(validateIBAN(""));
        assertFalse(validateIBAN(null));
        assertFalse(validateIBAN("DE19200411330823122700"));
        assertTrue(validateIBAN("DE18200411330823122700"));
        assertTrue(validateIBAN("DE89500105179394767432"));
        assertTrue(validateIBAN("DE44500105173481939824"));
        assertTrue(validateIBAN("NL79RABO2423554788"));
        assertTrue(validateIBAN("CH8589144649296413173"));
        assertTrue(validateIBAN("AT835400037618454391"));
    }
}