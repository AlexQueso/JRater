package compiler;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;

public class CompilerJava implements Compiler {

    private File temporalDir;

    public CompilerJava(File directory){
        temporalDir = directory;
    }

    @Override
    public String build() {
        throw new NotImplementedException();
    }
}
