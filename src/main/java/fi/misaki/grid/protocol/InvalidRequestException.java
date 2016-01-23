/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.protocol;

/**
 * Default exception for any errors that result in the termination of
 * user-initiated action.
 *
 * @author vlumi
 */
public class InvalidRequestException extends Exception {

    private static final long serialVersionUID = 9139910265348584806L;

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
