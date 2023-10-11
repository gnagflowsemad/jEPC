# jEPC

jEPC is a Java library for generating 12 lines of text as described the content of a QR code that can be used to initiate SEPA credit transfer (SCT).
It contains all the necessary information in clear text.

see also: https://en.wikipedia.org/wiki/EPC_QR_code

Generated text is to be used to generate a QR-Code. jEPC does not create a QR-Code by itself.
Use https://github.com/zxing/zxing for example.

## Get started

Example of text looks like:

                BCD
                002
                1
                SCT

                Wikimedia Foerdergesellschaft
                DE33100205000001194700
                EUR123.45
                BONU

                Spende
                Bitte innerhalb der naechsten 14 Tage ueberweisen

How to consume:

        Epc.Builder epc = new Epc.Builder();
        epc
                .withIssuer("Wikimedia Foerdergesellschaft")
                .withIBAN("DE33100205000001194700")
                .withTransferAmount(new BigDecimal("123.45"))
                .withSepaPurpose(SepaPurpose.BONU)
                .withIntendedUse("Spende")
                .withMessage("Bitte innerhalb der naechsten 14 Tage ueberweisen");

        String generated = epc.build();

