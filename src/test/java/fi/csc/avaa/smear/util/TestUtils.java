package fi.csc.avaa.smear.util;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestUtils {

    @SneakyThrows
    public static String expectedResponse(String filename) {
        Path path = Paths.get("src/test/resources/expected/" + filename);
        return Files.readString(path);
    }
}
