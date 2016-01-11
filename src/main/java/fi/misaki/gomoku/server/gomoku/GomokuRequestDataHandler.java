package fi.misaki.gomoku.server.gomoku;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import fi.misaki.gomoku.server.RequestDataHandler;
import fi.misaki.gomoku.server.user.User;
import fi.misaki.gomoku.server.user.UserManager;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
        // TODO: take action, based on input
        JsonObjectBuilder responseData = Json.createBuilderFactory(null)
                .createObjectBuilder();

    }

}
