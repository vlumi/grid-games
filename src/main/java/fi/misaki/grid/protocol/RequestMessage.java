package fi.misaki.grid.protocol;

import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.protocol.key.MessageKey;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import java.io.StringReader;

/**
 * Client-originating top-level message envelope.
 *
 * @author vlumi
 */
public class RequestMessage extends Message {

    private static final long serialVersionUID = -1406513789202879713L;

    /**
     * The request content as a JSON object, the exact format depending on the
     * message context.
     */
    private JsonObject data;

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
        super(MessageContext.ofCode(data.getString(MessageKey.CONTEXT.getCode(), "")));
        this.data = data.getJsonObject(MessageKey.DATA.getCode());
    }

    public JsonObject getData() {
        return data;
    }

    /**
     * Utility for parsing the JSON string into an object.
     *
     * @param data The data to parse.
     * @return The object parsed from the JSON string.
     * @throws InvalidRequestException
     */
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

    @Override
    public String toString() {
        return "RequestMessage{" + "data=" + data + "} < " + super.toString();
    }

}
