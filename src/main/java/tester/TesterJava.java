package tester;

import java.io.File;

public class TesterJava implements Tester{

    private File projectDir;

    public TesterJava(File projectDir) {
        this.projectDir = projectDir;
    }

    @Override
    public String test(String buildTrace) {
        throw new RuntimeException("not implemented yet");
    }
}
