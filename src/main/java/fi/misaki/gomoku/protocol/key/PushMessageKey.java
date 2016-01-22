package fi.misaki.gomoku.protocol.key;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vlumi
 */
public enum PushMessageKey {
    TYPE("type", true), // Inherited from MessageKey
    DATA("data", true),
    UNKNOWN();

    private final String code;
    private final boolean mandatory;

    private PushMessageKey() {
        this.code = "";
        this.mandatory = false;
    }

    private PushMessageKey(String code, boolean mandatory) {
        this.code = code;
        this.mandatory = mandatory;
    }

    public String getCode() {
        return code;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public static PushMessageKey ofCode(String code) {
        return Arrays.asList(PushMessageKey.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);
    }

    public static List<PushMessageKey> getMandatoryValues() {
        return Arrays.asList(PushMessageKey.values()).stream()
                .filter(value -> value.isMandatory())
                .collect(Collectors.toList());
    }

}
