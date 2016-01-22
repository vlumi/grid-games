package fi.misaki.gomoku.server.player;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestDataHandler;
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
public class PlayerRequestDataHandler extends RequestDataHandler {

    private static final long serialVersionUID = 8272657796866836750L;

    private static final Logger LOGGER = Logger.getLogger(PlayerRequestDataHandler.class.getName());

    @Inject
    private PlayerManager playerManager;

    /**
     *
     * @param session
     * @param data
     * @throws InvalidRequestException
     */
    @Override
    public void handleRequestData(Session session, JsonObject data)
            throws InvalidRequestException {

        switch (PlayerMessageDataType.ofCode(data.getString("type", ""))) {
            case LOGIN:
                playerManager.handleLoginRequest(session, data);
                break;
            default:
                throw new InvalidRequestException("Invalid player request type.");
        }

    }

}
