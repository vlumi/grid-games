package fi.misaki.gomoku.server.gomoku;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum GomokuMessageDataType {
    STATE("state"),
    JOIN(""),
    CHALLENGE("challenge"),
    ACCEPT("accept"),
    REJECT("reject"),
    MOVE("move"),
    UNKNOWN("");

    private final String code;

    private GomokuMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GomokuMessageDataType ofCode(String code) {
        return Arrays.asList(GomokuMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}
