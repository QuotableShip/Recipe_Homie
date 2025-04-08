import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonScripts {
    public static String runPythonScript(String GPTPrompt, String scriptPath) {
        try {

            // Create a process builder to run the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, GPTPrompt);

            // Start the process
            Process process = processBuilder.start();

            // Read the output from the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.out.println("Python script execution failed.");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return GPTPrompt;
    }
}
