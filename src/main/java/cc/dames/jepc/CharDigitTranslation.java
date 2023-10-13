package cc.dames.jepc;

public enum CharDigitTranslation {

    a("10"),
    b("11"),
    c("12"),
    d("13"),
    e("14"),
    f("15"),
    g("16"),
    h("17"),
    i("18"),
    j("19"),
    k("20"),
    l("21"),
    m("22"),
    n("23"),
    o("24"),
    p("25"),
    q("26"),
    r("27"),
    s("28"),
    t("29"),
    u("30"),
    v("31"),
    w("32"),
    x("33"),
    y("34"),
    z("35");

    private final String digit;

    public String getDigit() {
        return digit;
    }

    CharDigitTranslation(String digit) {
        this.digit = digit;
    }
}