import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;

public class Script {

    public static final String TEMPORAL_PROJECT = "temporal";
    public static final String REFERENCE_PROJECT = "reference";
    public static final String STUDENTS_PROJECTS = "students";

    public static void rateDirectory(File referenceProjectDir, File studentProjectsDir){
        throw new NotImplementedException();
    }

    public static void rateProject(File referenceProjectDir, File projectDir){
        createTemporalDir(referenceProjectDir);
    }

    public static void antiplag(File projectsDir, File configFile) {

    }

    /**
     * Creates a temporal directory, next to the reference project directory
     */
    private static void createTemporalDir(File referenceProject){
        //File currentDir = new File("").getAbsoluteFile();
        File projects = referenceProject.getParentFile();
        if (projects.listFiles() != null){
            for (File f: projects.listFiles()){
                if (f.getName().equals(TEMPORAL_PROJECT)){
                    if (!f.delete())
                        throw new RuntimeException("Failure deleting the directory:" + TEMPORAL_PROJECT);
                    break;
                }
            }
        }
        File temporalProjectDir = new File(projects, TEMPORAL_PROJECT);
        if (!temporalProjectDir.mkdir()){
            throw new RuntimeException("Failure creating the directory: " + TEMPORAL_PROJECT);
        }
    }
}
