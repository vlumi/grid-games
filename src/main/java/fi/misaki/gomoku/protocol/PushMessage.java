/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol;

import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.key.PushMessageKey;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Server-originating top-level message envelope.
 *
 * @author vlumi
 */
public class PushMessage extends Message {

    private static final long serialVersionUID = 5313709501800975594L;

    public PushMessage(MessageType type) {
        super(type);
    }

    /**
     * Message payload, the exact content depending on the message type.
     */
    private final JsonObjectBuilder payload = Json.createBuilderFactory(null)
            .createObjectBuilder();

    public JsonObjectBuilder getPayload() {
        return payload;
    }

    @Override
    protected JsonObjectBuilder toJsonObjectTemplate() {
        if (this.payload != null) {
            return super.toJsonObjectTemplate()
                    .add(PushMessageKey.PAYLOAD.getCode(), this.payload);
        }
        return super.toJsonObjectTemplate();
    }

    @Override
    public String toString() {
        return "PushMessage{" + "payload=" + payload + "} < " + super.toString();
    }

}
