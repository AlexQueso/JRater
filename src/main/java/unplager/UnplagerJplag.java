package unplager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnplagerJplag implements Unplager {

    @Override
    public String detect(File sourceDirectoy, File resultDirectory) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "java -jar src/main/resources/jplag.jar -l java19 -r " + resultDirectory.getPath() +
                "/Jplag -S src -s " + sourceDirectoy.getPath()  + " | grep -E \"Writing|successfully\"");
        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitVal = process.waitFor();
            if (exitVal != 0)
                throw new RuntimeException("jplag execution failure");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
