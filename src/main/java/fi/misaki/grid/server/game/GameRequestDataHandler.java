package fi.misaki.grid.server.game;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.server.RequestDataHandler;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.websocket.Session;
import java.util.logging.Logger;

/**
 * Request data handler for game messages.
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
     * Handle the request data of the received message.
     *
     * @param session The WebSocket session where the message was received from.
     * @param data    The data content of the received message.
     * @throws InvalidRequestException in case of any errors
     */
    @Override
    public void handleRequestData(Session session, JsonObject data) throws InvalidRequestException {

        Player player = userManager.getPlayerForSessionId(session.getId());

        switch (GameRequestMessageDataType.ofCode(data.getString("type", ""))) {
            case CHALLENGE:
                gameManager.handleChallengeRequest(player, data);
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
            default:
                throw new InvalidRequestException("Invalid game request type.");

        }
    }

}
