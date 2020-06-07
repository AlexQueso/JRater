package unplager;

import java.io.File;

public interface Unplager {

    String detect(File sourceDirectoy, File resultDirectory, String pathToJplag);
    
}
