package tester;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;

public class TesterJava implements Tester{

    private File projectDir;

    public TesterJava(File projectDir) {
        this.projectDir = projectDir;
    }

    @Override
    public String test(String buildTrace) {
        throw new NotImplementedException();
    }
}
