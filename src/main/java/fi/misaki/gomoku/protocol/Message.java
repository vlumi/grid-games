package fi.misaki.gomoku.protocol;

import fi.misaki.gomoku.protocol.key.MessageKey;
import fi.misaki.gomoku.protocol.key.MessageType;
import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Top-level message envelope.
 *
 * @author vlumi
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 2402910556901021302L;

    /**
     * The type of the message, as defined in the enum MessageType.
     */
    private final MessageType type;

    public Message() {
        this.type = MessageType.UNKNOWN;
    }

    public Message(MessageType type) {
        this.type = type;
    }

    public JsonObject toJsonObject() {
        return this.toJsonObjectTemplate().build();
    }

    public MessageType getType() {
        return type;
    }

    protected JsonObjectBuilder toJsonObjectTemplate() {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        return factory.createObjectBuilder()
                .add(MessageKey.TYPE.getCode(), type.getCode());
    }

    @Override
    public String toString() {
        return "AbstractMessage{" + "type=" + type + '}';
    }

}
