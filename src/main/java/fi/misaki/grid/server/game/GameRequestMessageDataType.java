package fi.misaki.grid.server.game;

import java.util.Arrays;

/**
 * Message data types for game request messages.
 *
 * @author vlumi
 */
public enum GameRequestMessageDataType {
    /**
     * Challenge another player.
     */
    CHALLENGE("challenge"),
    /**
     * Accept a received challenge.
     */
    ACCEPT_CHALLENGE("acceptChallenge"),
    /**
     * Reject a received challenge.
     */
    REJECT_CHALLENGE("rejectChallenge"),
    /**
     * Place a piece on the board.
     */
    PLACE_PIECE("placePiece"),
    /**
     * Start a new game, after game over.
     */
    NEW_GAME("newGame"),
    /**
     * Leave the game.
     */
    LEAVE("leave"),
    UNKNOWN("");

    private final String code;

    private GameRequestMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GameRequestMessageDataType ofCode(String code) {
        return Arrays.asList(GameRequestMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

}
