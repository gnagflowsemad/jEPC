package cc.dames.jepc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cc.dames.jepc.SepaUtils.*;
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
    public void validSCORTest() {
        assertTrue(validSCOR("RF18 5390 0754 7034"));
        assertTrue(validSCOR(""));
        assertTrue(validSCOR(null));
        assertTrue(validSCOR("RF18000000000539007547034"));
        assertFalse(validSCOR("rf18000000000539007547034"));
        assertFalse(validSCOR("RV18000000000539007547034"));
        assertFalse(validSCOR("RF180000000005390075470345"));
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
}