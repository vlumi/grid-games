/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol;

import fi.misaki.gomoku.protocol.key.MessageType;
import fi.misaki.gomoku.protocol.key.ErrorMessageKey;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author vlumi
 */
public class ErrorMessage extends Message {

    private static final long serialVersionUID = 5389843124148667693L;

    private final String text;

    public ErrorMessage(String text) {
        super(MessageType.ERROR);
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
        return "Response{" + "text=" + text + "} < " + super.toString();
    }

}
