package fi.misaki.grid.server.player;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum PlayerRequestMessageDataType {
    /**
     * Login message.
     */
    LOGIN("login"),
    UNKNOWN("");

    private final String code;

    private PlayerRequestMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PlayerRequestMessageDataType ofCode(String code) {
        return Arrays.asList(PlayerRequestMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
