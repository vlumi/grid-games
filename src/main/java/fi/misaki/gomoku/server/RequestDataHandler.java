package fi.misaki.gomoku.server;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import java.io.Serializable;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
public abstract class RequestDataHandler implements Serializable {

    private static final long serialVersionUID = -6541557565273505255L;

    /**
     *
     * @param session
     * @param data
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public abstract void handleRequestData(Session session, JsonObject data)
            throws InvalidRequestException;

}
