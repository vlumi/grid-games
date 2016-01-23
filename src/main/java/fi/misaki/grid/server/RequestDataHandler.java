package fi.misaki.grid.server;

import fi.misaki.grid.protocol.InvalidRequestException;
import java.io.Serializable;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 * Abstract class for all request data handlers.
 *
 * @author vlumi
 */
public abstract class RequestDataHandler implements Serializable {

    private static final long serialVersionUID = -6541557565273505255L;

    /**
     * Handle the request message data part received from the client.
     *
     * @param session Current session.
     * @param data The data object received from the client.
     * @throws fi.misaki.grid.protocol.InvalidRequestException
     */
    public abstract void handleRequestData(Session session, JsonObject data)
            throws InvalidRequestException;

}
