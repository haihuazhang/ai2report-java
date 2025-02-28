package customer.aireport.exception;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;

public class AIServiceException extends ServiceException {
    public AIServiceException(String message) {
        super(ErrorStatuses.BAD_REQUEST, message);
    }

    public AIServiceException(String message, Throwable cause) {
        super(ErrorStatuses.BAD_REQUEST, message, cause);
    }

    public static AIServiceException notFound(String entity) {
        return new AIServiceException(entity + "_not_found");
    }

    public static AIServiceException parsingError(String type, Throwable cause) {
        return new AIServiceException("Error_When_Parsing_" + type, cause);
    }
}