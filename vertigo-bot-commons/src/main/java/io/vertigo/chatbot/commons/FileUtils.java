package io.vertigo.chatbot.commons;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Base64;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;

/**
 * @author cmarechal
 * @created 03/06/2024 - 11:10
 * @project vertigo-bot-factory
 */
public class FileUtils {

    public static ByteArrayInputStream base64StringToByteArrayInputStream(String base64File, String fileName) {
        byte[] decodedBytes = Base64.getDecoder().decode(fileContentFromBase64String(base64File, fileName));
        return new ByteArrayInputStream(decodedBytes);
    }

    public static long fileSizeFromBase64String(String base64File, String fileName) {
        String fileContent = fileContentFromBase64String(base64File, fileName);
        String contentWithoutPadding = fileContent.replaceAll("=", "");
        return (contentWithoutPadding.getBytes().length * 3L) / 4;
    }

    public static String fileContentFromBase64String(String base64File, String fileName) {
        final String[] fileDataTable = base64File.split(",");
        Assertion.check().isTrue(fileDataTable.length == 2, "Attachment " + fileName + " is not Base64 encoded");
        return fileDataTable[1];
    }

    public static String formatImageToBase64String(final HttpResponse<InputStream> response) {
        String imageB64;
        String extension = response.headers().map().get("content-type").get(0);
        if (extension == null){
            throw new VSystemException("File extension cannot be null");
        }
        try {
            byte[] bytes = IOUtils.toByteArray(response.body());
            imageB64 = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error during Base64 file encoding");
        }
        return "data:" + extension + ";base64," + imageB64;
    }
}
