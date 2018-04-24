package uk.gov.cshr.atsadaptor.service.util;

import java.io.IOException;
import java.nio.file.Path;

import uk.gov.cshr.exception.CSHRServiceException;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

public final class PathUtil {
    private PathUtil() {}

    public static void createFile(Path path) {
        try {
            path.toFile().createNewFile();
        } catch (IOException e) {
            CSHRServiceStatus status = CSHRServiceStatus
                    .builder()
                    .code(StatusCode.FILE_SYSTEM_ERROR.getCode())
                    .summary(e.getMessage())
                    .build();

            throw CSHRServiceException
                    .builder()
                    .cshrServiceStatus(status)
                    .build();
        }
    }
}
