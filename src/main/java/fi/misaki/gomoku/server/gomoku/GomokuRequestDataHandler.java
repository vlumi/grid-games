package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestDataHandler;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
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
public class GomokuRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 7531558038666358138L;

    private static final Logger LOGGER = Logger.getLogger(GomokuRequestDataHandler.class.getName());

    @Inject
    private GomokuManager gomokuManager;
    @Inject
    private UserManager userManager;

    /**
     *
     * @param session
     * @param data
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestData(Session session, JsonObject data) throws InvalidRequestException {

        User user = userManager.getUserForSessionId(session.getId());

        // TODO: parse the input
        switch (GomokuMessageDataType.ofCode(data.getString("type", ""))) {
            case STATE:
                gomokuManager.handleStateRequest(user, data);
                break;
            case CHALLENGE:
                gomokuManager.handleChallengeRequest(user, data);
                break;
            case CANCEL_CHALLENGE:
                gomokuManager.handleCancelChallengeRequest(user, data);
                break;
            case ACCEPT_CHALLENGE:
                gomokuManager.handleAcceptChallengeRequest(user, data);
                break;
            case REJECT_CHALLENGE:
                gomokuManager.handleRejectChallengeRequest(user, data);
                break;
            case PLACE_PIECE:
                gomokuManager.handlePlacePieceRequest(user, data);
                break;
            case LEAVE:
                gomokuManager.handleLeaveRequest(user, data);
            case GAME_OVER:
            default:
                throw new InvalidRequestException("Invalid gomoku request type.");

        }
    }

}
