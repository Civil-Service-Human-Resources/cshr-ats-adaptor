package uk.gov.cshr.atsadaptor.service.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import uk.gov.cshr.exception.CSHRServiceException;
import uk.gov.cshr.status.CSHRServiceStatus;
import uk.gov.cshr.status.StatusCode;

/**
 * This class provides utility methods for working with Paths in the adaptor
 */
public final class PathUtil {
    private PathUtil() {}

    /**
     * Creates a new file for the given path if one does not already exist.
     *
     * The method will throw an CSHRServicException if there are any issues working with the file.
     *
     * @param path location of file to be created if one does not already exist.
     */
    public static void createFileIfRequired(Path path) {
        try {
            if (path.toFile().exists()) {
                Files.delete(path);
            }

            path.toFile().createNewFile();
        } catch (Exception e) {
            List<String> details = new ArrayList<>();
            details.add("Name of file path is: " + path.toString());

            CSHRServiceStatus status = CSHRServiceStatus
                    .builder()
                    .code(StatusCode.FILE_SYSTEM_ERROR.getCode())
                    .summary(e.getMessage())
                    .detail(details)
                    .build();

            throw CSHRServiceException
                    .builder()
                    .cshrServiceStatus(status)
                    .build();
        }
    }
}
