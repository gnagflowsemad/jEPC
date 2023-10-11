package cc.dames.jepc;

public enum Version {
    _001("001"),
    _002("002");

    private final String code;

    public String getCode() {
        return code;
    }

    Version(String code) {
        this.code = code;
    }
}
