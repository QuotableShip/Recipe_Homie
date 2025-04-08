import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {
    public static String findLatestFileInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles(File::isFile);

        if (files == null || files.length == 0) {
            throw new RuntimeException("No files found in the directory: " + directoryPath);
        }

        File latestFile = Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElseThrow(() -> new RuntimeException("No files found in the directory: " + directoryPath));

        return latestFile.getAbsolutePath();
    }


    // Read the contents from a file
    public static String readRecipeFromFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.ISO_8859_1)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

        // Print error if the file encoding is incompatible
        } catch (MalformedInputException e) {
            System.err.println("MalformedInputException: Error reading the file. Please check the file encoding.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: Error reading the file.");
            e.printStackTrace();
        }
        return content.toString();
    }

    // Extract the title from the recipe content
    public static String extractTitle(String recipeContent) {
        for (String line : recipeContent.split("\n")) {
            if (line.startsWith("Title:")) {
                return line.substring(7).trim(); // Extract the title after "Title: "
            }
        }
        return null;
    }
}
