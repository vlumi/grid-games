package fi.misaki.grid.server.player;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum PlayerMessageDataType {
    LOGIN("login"),
    ERROR("error"),
    UNKNOWN("");

    private final String code;

    private PlayerMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PlayerMessageDataType ofCode(String code) {
        return Arrays.asList(PlayerMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
