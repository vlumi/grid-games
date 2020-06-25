package fi.misaki.grid.server.player;

import java.util.Arrays;

/**
 * @author vlumi
 */
public enum PlayerPushMessageDataType {
    /**
     * Player login information.
     */
    LOGIN("login"),
    /**
     * Login error message.
     */
    ERROR("error"),
    UNKNOWN("");

    private final String code;

    private PlayerPushMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PlayerPushMessageDataType ofCode(String code) {
        return Arrays.asList(PlayerPushMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
