package cc.dames.jepc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cc.dames.jepc.SepaUtils.*;
import static cc.dames.jepc.SepaUtils.validateSCOR;
import static org.junit.jupiter.api.Assertions.*;

class SepaUtilsTest {

    @Test
    public void sepaTextPatternTest() {
        assertTrue(SEPA_TEXT_PATTERN.matcher("Wikimedia Foerdergesellschaft").matches());
        assertTrue(SEPA_TEXT_PATTERN.matcher("Bitte innerhalb der naechsten 14 Tage ueberweisen").matches());
        assertTrue(SEPA_TEXT_PATTERN.matcher("A&B Events").matches());
        assertFalse(SEPA_TEXT_PATTERN.matcher("Wikimedia Fördergesellschaft").matches());
        assertTrue(SEPA_TEXT_UMLAUTS_PATTERN.matcher("Wikimedia Fördergesellschaft").matches());
        assertTrue(SEPA_TEXT_UMLAUTS_PATTERN.matcher("A&B Events").matches());
    }

    @Test
    public void maxAmountTest() {
        assertFalse(exceedAmount(new BigDecimal("0.01")));
        assertFalse(exceedAmount(new BigDecimal("999999999.99")));
        assertTrue(exceedAmount(new BigDecimal("-0.01")));
        assertTrue(exceedAmount(new BigDecimal("0")));
        assertTrue(exceedAmount(BigDecimal.ZERO));
        assertTrue(exceedAmount(null));
        assertTrue(exceedAmount(new BigDecimal("1000000000")));
    }

    @Test
    public void strEmptyTest() {
        assertTrue(strEmpty(""));
        assertFalse(strEmpty("A"));
        assertTrue(strEmpty(null));
    }

    @Test
    public void strNotEmptyTest() {
        assertTrue(strNotEmpty(" "));
        assertTrue(strNotEmpty("A"));
        assertFalse(strNotEmpty(""));
    }

    @Test
    public void validateSCORTest() {
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
    public void createSCORTest() {
        assertEquals("RF794723M108", createSCOR("4723M108"));
    }
}