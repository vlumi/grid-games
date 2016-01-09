/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol;

import fi.misaki.gomoku.protocol.key.ResponseKey;
import javax.json.JsonObjectBuilder;

/**
 * Server-originating top-level message envelope, which is a direct response to
 * a request message from the client.
 *
 * @author vlumi
 */
public class Response extends PushMessage {

    private static final long serialVersionUID = -6897659003050116802L;

    private boolean error = false;
    private String message = "";

    public boolean isError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected JsonObjectBuilder toJsonObjectTemplate() {
        return super.toJsonObjectTemplate()
                .add(ResponseKey.ERROR.getCode(), this.error)
                .add(ResponseKey.MESSAGE.getCode(), this.message);
    }

    @Override
    public String toString() {
        return "Response{" + "error=" + error + ", message=" + message + "} < " + super.toString();
    }

}
