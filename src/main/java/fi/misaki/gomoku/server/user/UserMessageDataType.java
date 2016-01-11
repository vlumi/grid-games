package fi.misaki.gomoku.server.user;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum UserMessageDataType {
    LOGIN("login"),
    UNKNOWN("");

    private final String code;

    private UserMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static UserMessageDataType ofCode(String code) {
        return Arrays.asList(UserMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
