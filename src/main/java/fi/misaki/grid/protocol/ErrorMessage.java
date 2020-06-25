package fi.misaki.grid.protocol;

import fi.misaki.grid.protocol.key.ErrorMessageKey;
import fi.misaki.grid.protocol.key.MessageContext;

import javax.json.JsonObjectBuilder;

/**
 * Server-originating error message.
 *
 * @author vlumi
 */
public class ErrorMessage extends Message {

    private static final long serialVersionUID = 5389843124148667693L;

    private final String text;

    public ErrorMessage(String text) {
        super(MessageContext.ERROR);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected JsonObjectBuilder toJsonObjectTemplate() {
        return super.toJsonObjectTemplate()
                .add(ErrorMessageKey.TEXT.getCode(), this.text);
    }

    @Override
    public String toString() {
        return "ErrorMessage{" + "text=" + text + "} < " + super.toString();
    }

}
