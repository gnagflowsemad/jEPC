package cc.dames.jepc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EpcTest {

    @Test
    public void testMinimalEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende");

        String generated = epc.build();

        String expected = """
                BCD
                002
                1
                SCT

                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45


                Spende
                """;

        assertEquals(expected, generated);
        assertEquals(11, generated.split("\n").length);
    }

    @Test
    public void testStringAmountEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount("123.45")
                .withIntendedUse("Spende");

        String generated = epc.build();

        String expected = """
                BCD
                002
                1
                SCT

                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45


                Spende
                """;

        assertEquals(expected, generated);
        assertEquals(11, generated.split("\n").length);
    }

    @Test
    public void testNegativeAmountEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("-123.45"))
                .withIntendedUse("Spende");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("transfer amount is out of valid range"));
    }

    @Test
    public void testAmountExceedEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("1000000000.01"))
                .withIntendedUse("Spende");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("transfer amount is out of valid range"));
    }

    @Test
    public void testCharacterEncodingEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withCharacterEncoding(2)
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende");

        String generated = epc.build();

        String expected = """
                BCD
                002
                2
                SCT

                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45


                Spende
                """;

        assertEquals(expected, generated);
        assertEquals(11, generated.split("\n").length);
    }

    @Test
    public void testWrongCharacterEncodingEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withCharacterEncoding(9)
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("character encoding must be between 1 and 8"));
    }

    @Test
    public void testSimpleEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der naechsten 14 Tage ueberweisen");

        String generated = epc.build();

        String expected = """
                BCD
                002
                1
                SCT

                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45


                Spende
                Bitte innerhalb der naechsten 14 Tage ueberweisen""";

        assertEquals(expected, generated);
        assertEquals(12, generated.split("\n").length);
    }

    @Test
    public void testSepaPurposeEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withSepaPurpose(SepaPurpose.BONU)
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der naechsten 14 Tage ueberweisen");

        String generated = epc.build();

        String expected = """
                BCD
                002
                1
                SCT

                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45
                BONU

                Spende
                Bitte innerhalb der naechsten 14 Tage ueberweisen""";

        assertEquals(expected, generated);
        assertEquals(12, generated.split("\n").length);
    }

    @Test
    public void testCRLFEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withLineFeed(LineFeed.CRLF)
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der naechsten 14 Tage ueberweisen");

        String generated = epc.build();

        assertEquals(12, generated.split("\r\n").length);
    }

    @Test
    public void testUmlautsPositiveEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Fördergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende")
                .withUmlauts(true)
                .withMessage("Bitte innerhalb der nächsten 14 Tage überweisen");

        String generated = epc.build();

        String expected = """
                BCD
                002
                1
                SCT

                Wikimedia Fördergesellschaft
                DE33100205000001194700
                EUR123.45


                Spende
                Bitte innerhalb der nächsten 14 Tage überweisen""";

        assertEquals(expected, generated);
        assertEquals(12, generated.split("\n").length);
    }

    @Test
    public void testUmlautsNegativeEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der nächsten 14 Tage überweisen");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("message contains invalid character(s)"));
    }

    @Test
    public void testSCOREpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withScor("RF18000000000539007547034")
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der nächsten 14 Tage überweisen");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("either SCOR or intended use can be set"));
    }

    @Test
    public void testInvalidSCOREpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withScor("RF180000000005390075470345")
                .withMessage("Bitte innerhalb der nächsten 14 Tage überweisen");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("SCOR has invalid format"));
    }

    @Test
    public void testVersionWithoutBICEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withVersion(Version._001)
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withMessage("Bitte innerhalb der nächsten 14 Tage überweisen");

        EpcException thrown = assertThrows(EpcException.class, epc::build);
        assertTrue(thrown.getMessage().contains("BIC can not be empty if version is 001"));
    }

    @Test
    public void testVersionWithBICEpc() {
        Epc.Builder epc = new Epc.Builder();
        epc
                .withVersion(Version._001)
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withBIC("BFSWDE33BER")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der naechsten 14 Tage ueberweisen");

        String generated = epc.build();

        String expected = """
                BCD
                001
                1
                SCT
                BFSWDE33BER
                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45


                Spende
                Bitte innerhalb der naechsten 14 Tage ueberweisen""";

        assertEquals(expected, generated);
        assertEquals(12, generated.split("\n").length);
    }

}
