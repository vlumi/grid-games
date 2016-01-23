package fi.misaki.grid.server.lobby;

import fi.misaki.grid.protocol.InvalidRequestException;
import fi.misaki.grid.server.RequestDataHandler;
import fi.misaki.grid.server.player.Player;
import fi.misaki.grid.server.player.PlayerManager;
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
    private PlayerManager playerManager;
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

        Player player = playerManager.getPlayerForSessionId(session.getId());

        switch (LobbyMessageDataType.ofCode(data.getString("type", ""))) {
            case CHAT_MESSAGE:
                lobbyManager.handleChatMessageRequest(player, data);
                break;
            default:
                throw new InvalidRequestException("Invalid lobby request type.");
        }
    }

}
