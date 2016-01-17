package fi.misaki.gomoku.server.lobby;

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
public class LobbyRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 7826083979194479480L;

    private static final Logger LOGGER = Logger.getLogger(LobbyRequestDataHandler.class.getName());

    @Inject
    private UserManager userManager;
    @Inject
    private LobbyManager lobbyManager;

    /**
     *
     * @param session
     * @param data
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestData(Session session, JsonObject data) throws InvalidRequestException {

        User user = userManager.getUserForSessionId(session.getId());

        switch (LobbyMessageDataType.ofCode(data.getString("type", ""))) {
            case CHAT_MESSAGE:
                lobbyManager.handleChatMessageRequest(user, data);
                break;
            default:
                throw new InvalidRequestException("Invalid lobby request type.");
        }
    }

}
