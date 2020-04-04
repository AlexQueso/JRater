package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CompilerAntJava implements Compiler {

    private File temporalDir;

    public CompilerAntJava(File directory){
        temporalDir = directory;
    }

    @Override
    public String build() {
        System.out.println("Building project ...");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "ant -f " + temporalDir.toString() + "/build.xml compile | grep \"BUILD\"");
        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitVal = process.waitFor();
            if (exitVal != 0)
                throw new RuntimeException("Build failure");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}