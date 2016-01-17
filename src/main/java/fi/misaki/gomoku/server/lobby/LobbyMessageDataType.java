package fi.misaki.gomoku.server.lobby;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum LobbyMessageDataType {
    INIT("init"),
    JOIN("join"),
    PART("part"),
    CHAT_MESSAGE("chatMessage"),
    STATUS("status"),
    UNKNOWN("");

    private final String code;

    private LobbyMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LobbyMessageDataType ofCode(String code) {
        return Arrays.asList(LobbyMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
