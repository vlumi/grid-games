package fi.misaki.grid.server.game;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum GameMessageDataType {
    // TODO: split to request/response?
    STATE("state"),
    CHALLENGE("challenge"),
    ACCEPT_CHALLENGE("acceptChallenge"),
    REJECT_CHALLENGE("rejectChallenge"),
    PLACE_PIECE("placePiece"),
    FORFEIT("forfeit"),
    GAME_OVER("gameOver"),
    NEW_GAME("newGame"),
    LEAVE("leave"),
    UNKNOWN("");

    private final String code;

    private GameMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GameMessageDataType ofCode(String code) {
        return Arrays.asList(GameMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}
