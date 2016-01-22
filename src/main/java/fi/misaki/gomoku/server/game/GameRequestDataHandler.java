package fi.misaki.gomoku.server.game;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestDataHandler;
import fi.misaki.gomoku.server.player.Player;
import fi.misaki.gomoku.server.player.PlayerManager;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
@Stateless
public class GameRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 7531558038666358138L;

    private static final Logger LOGGER = Logger.getLogger(GameRequestDataHandler.class.getName());

    @Inject
    private GameManager gameManager;
    @Inject
    private PlayerManager userManager;

    /**
     *
     * @param session
     * @param data
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestData(Session session, JsonObject data) throws InvalidRequestException {

        Player player = userManager.getPlayerForSessionId(session.getId());

        // TODO: parse the input
        switch (GameMessageDataType.ofCode(data.getString("type", ""))) {
            case CHALLENGE:
                gameManager.handleChallengeRequest(player, data);
                break;
            case CANCEL_CHALLENGE:
                gameManager.handleCancelChallengeRequest(player, data);
                break;
            case ACCEPT_CHALLENGE:
                gameManager.handleAcceptChallengeRequest(player, data);
                break;
            case REJECT_CHALLENGE:
                gameManager.handleRejectChallengeRequest(player, data);
                break;
            case PLACE_PIECE:
                gameManager.handlePlacePieceRequest(player, data);
                break;
            case NEW_GAME:
                gameManager.handleNewGame(player, data);
                break;
            case LEAVE:
                gameManager.handleLeaveRequest(player, data);
                break;
            case GAME_OVER:
            default:
                throw new InvalidRequestException("Invalid gomoku request type.");

        }
    }

}
