package cc.dames.jepc;

public enum LineFeed {
    LF("\n"),
    CRLF("\r\n");

    public String getCode() {
        return code;
    }

    LineFeed(String code) {
        this.code = code;
    }

    private final String code;
}
