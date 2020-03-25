package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class TesterAntJava implements Tester{

    private File projectDir;

    public TesterAntJava(File projectDir) {
        this.projectDir = projectDir;
    }

    @Override
    public String test(String buildTrace) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "ant test | grep -E \"Testsuite|Tests run:|Testcase|\\[junit\\] java.|\\[junit\\] junit.|at \"");
        StringBuilder output = new StringBuilder();
        try{
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal != 0)
                throw new RuntimeException("Test execution failure");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
