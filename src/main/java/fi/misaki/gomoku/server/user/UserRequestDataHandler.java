package fi.misaki.gomoku.server.user;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestDataHandler;
import fi.misaki.gomoku.server.lobby.LobbyManager;
import java.util.logging.Level;
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
public class UserRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 8272657796866836750L;

    private static final Logger LOGGER = Logger.getLogger(UserRequestDataHandler.class.getName());

    @Inject
    private UserManager userManager;

    /**
     *
     * @param session
     * @param data
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestData(Session session, JsonObject data)
            throws InvalidRequestException {

        switch (UserMessageDataType.ofCode(data.getString("type", ""))) {
            case LOGIN:
                userManager.handleLoginRequest(session, data);
                break;
            default:
                throw new InvalidRequestException("Invalid user request type.");
        }

    }

}
