package cc.dames.jepc;

public enum Version {
    V001("001"),
    V002("002");

    private final String code;

    public String getCode() {
        return code;
    }

    Version(String code) {
        this.code = code;
    }
}
