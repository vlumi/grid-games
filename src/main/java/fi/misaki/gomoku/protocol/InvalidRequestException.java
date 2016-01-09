/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol;

/**
 *
 * @author vlumi
 */
public class InvalidRequestException extends Exception {

    private static final long serialVersionUID = 9139910265348584806L;

    /**
     *
     */
    public InvalidRequestException() {
        super();
    }

    /**
     *
     * @param message
     */
    public InvalidRequestException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
