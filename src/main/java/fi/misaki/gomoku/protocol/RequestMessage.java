/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol;

import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.key.MessageKey;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;

/**
 * Client-originating top-level message envelope.
 *
 * @author vlumi
 */
public class RequestMessage extends Message {

    private static final long serialVersionUID = -1406513789202879713L;

    /**
     * The request content as a JSON object, the exact format depending on the
     * message type.
     */
    private JsonObject payload;

    /**
     * Create the request object from the JSON string.
     *
     * @param data The request as a JSON string.
     * @throws InvalidRequestException
     */
    public RequestMessage(String data)
            throws InvalidRequestException {
        this(parseFromJsonString(data));
    }

    /**
     * Create the request object from the JSON object.
     *
     * @param data The request as a JSON object.
     */
    public RequestMessage(JsonObject data) {
        super(MessageType.ofCode(data.getString(MessageKey.TYPE.getCode(), "")));
        this.payload = data.getJsonObject(MessageKey.PAYLOAD.getCode());
    }

    public JsonObject getPayload() {
        return payload;
    }

    private static JsonObject parseFromJsonString(String data)
            throws InvalidRequestException {
        try {
            return Json
                    .createReader(new StringReader(data))
                    .readObject();
        } catch (JsonException e) {
            throw new InvalidRequestException("Invalid request.");

        }
    }

}
