package fi.misaki.grid.server.game;

import java.util.Arrays;

/**
 * Message data types for push messages.
 *
 * @author vlumi
 */
public enum GamePushMessageDataType {
    /**
     * Game state to players.
     */
    STATE("state"),
    /**
     * Relay game challenge from another player.
     */
    CHALLENGE("challenge"),
    /**
     * Relay a piece being placed on the board.
     */
    PLACE_PIECE("placePiece"),
    /**
     * Relay game over information.
     */
    GAME_OVER("gameOver"),
    /**
     * Relay a player leaving the game, terminating it.
     */
    LEAVE("leave"),
    UNKNOWN("");

    private final String code;

    private GamePushMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GamePushMessageDataType ofCode(String code) {
        return Arrays.asList(GamePushMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}
