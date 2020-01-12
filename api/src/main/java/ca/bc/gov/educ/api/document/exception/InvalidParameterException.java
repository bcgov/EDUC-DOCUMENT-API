package ca.bc.gov.educ.api.document.exception;

/**
 * InvalidParameterException to provide error details when unexpected parameters are passed to endpoint
 *
 * @author John Cox
 *
 */

public class InvalidParameterException extends RuntimeException {

    private static final long serialVersionUID = 8926815015510650437L;

    public InvalidParameterException(String... paramsMap) {
        super(InvalidParameterException.generateMessage(paramsMap));
    }

    private static String generateMessage(String... paramsMap) {
        String message = "Unexpected request parameters provided: ";
        return message + String.join(", ", paramsMap);
    }
}
