package fi.misaki.grid.protocol.key;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Top-level fields for the message envelope.
 *
 * @author vlumi
 */
public enum MessageKey {
    CONTEXT("context", true),
    DATA("data", false),
    UNKNOWN();

    private final String code;
    private final boolean mandatory;

    private MessageKey() {
        this.code = "";
        this.mandatory = false;
    }

    private MessageKey(String code, boolean mandatory) {
        this.code = code;
        this.mandatory = mandatory;
    }

    public String getCode() {
        return code;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public static MessageKey ofCode(String code) {
        return Arrays.asList(MessageKey.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

    public static List<MessageKey> getValidValues() {
        return Arrays.asList(MessageKey.values()).stream()
                .filter(value -> value != MessageKey.UNKNOWN)
                .collect(Collectors.toList());
    }

    public static List<MessageKey> getMandatoryValues() {
        return Arrays.asList(MessageKey.values()).stream()
                .filter(value -> value.isMandatory())
                .collect(Collectors.toList());
    }

}
