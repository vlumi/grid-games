package fi.misaki.gomoku.protocol.key;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vlumi
 */
public enum ErrorMessageKey {
    TYPE("type", true), // Inherited from MessageKey
    TEXT("text", false),
    UNKNOWN();

    private final String code;
    private final boolean mandatory;

    private ErrorMessageKey() {
        this.code = "";
        this.mandatory = false;
    }

    private ErrorMessageKey(String code, boolean mandatory) {
        this.code = code;
        this.mandatory = mandatory;
    }

    public String getCode() {
        return code;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public static ErrorMessageKey ofCode(String code) {
        return Arrays.asList(ErrorMessageKey.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

    public static List<ErrorMessageKey> getMandatoryValues() {
       return Arrays.asList(ErrorMessageKey.values()).stream()
                .filter(value -> value.isMandatory())
                .collect(Collectors.toList());
     }
}
