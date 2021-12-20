package fi.misaki.grid.protocol;

import fi.misaki.grid.protocol.key.MessageContext;
import fi.misaki.grid.protocol.key.PushMessageKey;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

/**
 * Server-originating top-level message envelope.
 *
 * @author vlumi
 */
public class PushMessage extends Message {

    private static final long serialVersionUID = 5313709501800975594L;

    public PushMessage(MessageContext context) {
        super(context);
    }

    /**
     * Message data, the exact content depending on the message type.
     */
    private final JsonObjectBuilder data = Json.createBuilderFactory(null)
            .createObjectBuilder();

    public JsonObjectBuilder getData() {
        return data;
    }

    @Override
    protected JsonObjectBuilder toJsonObjectTemplate() {
        if (this.data != null) {
            return super.toJsonObjectTemplate()
                    .add(PushMessageKey.DATA.getCode(), this.data);
        }
        return super.toJsonObjectTemplate();
    }

    @Override
    public String toString() {
        return "PushMessage{" + "data=" + data + "} < " + super.toString();
    }

}
