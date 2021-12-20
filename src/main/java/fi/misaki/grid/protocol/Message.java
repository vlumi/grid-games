package fi.misaki.grid.protocol;

import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.protocol.key.MessageKey;

import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.Serializable;

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
    private final MessageContext context;

    public Message() {
        this.context = MessageContext.UNKNOWN;
    }

    public Message(MessageContext context) {
        this.context = context;
    }

    public JsonObject toJsonObject() {
        return this.toJsonObjectTemplate().build();
    }

    public MessageContext getContext() {
        return context;
    }

    protected JsonObjectBuilder toJsonObjectTemplate() {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        return factory.createObjectBuilder()
                .add(MessageKey.CONTEXT.getCode(), context.getCode());
    }

    @Override
    public String toString() {
        return "AbstractMessage{" + "context=" + context + '}';
    }

}
