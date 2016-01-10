/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server;

import fi.misaki.gomoku.protocol.InvalidRequestException;
import java.io.Serializable;
import javax.json.JsonObject;
import javax.websocket.Session;

/**
 *
 * @author vlumi
 */
public abstract class RequestPayloadHandler implements Serializable {

    private static final long serialVersionUID = -6541557565273505255L;

    /**
     *
     * @param session
     * @param payload
     * @throws fi.misaki.gomoku.protocol.InvalidRequestException
     */
    public abstract void handleRequestPayload(Session session, JsonObject payload)
            throws InvalidRequestException;

}
