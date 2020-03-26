package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class TesterAntJava implements Tester{

    private File temporalDir;

    public TesterAntJava(File projectDir) {
        this.temporalDir = projectDir;
    }

    @Override
    public String test(String buildTrace) {
        System.out.println("Executing tests ...");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "ant -f " + temporalDir.toString() + "/build.xml test | grep -E " +
                "\"Testsuite|Testcase|Tests run|\\[junit\\] java.|\\[junit\\] junit.|at |org.junit does not exist\"");
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
        String s = output.toString();
        if (s.contains("org.junit does not exist"))
            throw new RuntimeException("Failure during testing phase, junit dependencies weren't found");
        return s;
    }
}
