import compiler.Compiler;
import compiler.CompilerAntJava;
import org.apache.commons.io.FileUtils;
import reports.BuildTestReportGenerator;
import tester.Tester;
import tester.TesterJava;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Script {

    public static final String TEMPORAL_PROJECT = "temporal";
    public static final String REFERENCE_PROJECT = "reference";
    public static final String STUDENTS_PROJECTS = "students";

    public static void rateDirectory(File referenceProject, File studentProject){
        throw new RuntimeException("not implemented yet");
    }

    public static void rateProject(File referenceProject, File studentProject){
        File tempDir = createTemporalDir(referenceProject);
        fillTemporalProject(tempDir, referenceProject, studentProject);

        Compiler compiler = new CompilerAntJava(tempDir);
        String buildTrace = compiler.build();

        Tester tester = new TesterJava(tempDir);
        String testTrace = tester.test(buildTrace);

        BuildTestReportGenerator.generate(buildTrace, testTrace);
    }

    public static void antiplag(File studentProjects, File configFile) {
    }

    /**
     * Creates a temporal directory in the same directory that the reference project directory
     */
    private static File createTemporalDir(File referenceProject){
        File projects = referenceProject.getParentFile();
        if (projects.listFiles() != null){
            for (File f: Objects.requireNonNull(projects.listFiles())){
                if (f.getName().equals(TEMPORAL_PROJECT)){
                    try {
                        FileUtils.deleteDirectory(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        File temporalProjectDir = new File(projects, TEMPORAL_PROJECT);
        try {
            FileUtils.forceMkdir(temporalProjectDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temporalProjectDir;
    }

    /**
     * Fill the temporal project directory with a copy of the reference project and the src directory of the
     * student project.
     */
    private static void fillTemporalProject(File temporalDirectory, File referenceProject, File studentProject){
        try {
            FileUtils.copyDirectory(referenceProject, temporalDirectory);
            File srcDir = new File(temporalDirectory, "src");

            if (temporalDirectory.listFiles() != null){
                for (File f: Objects.requireNonNull(temporalDirectory.listFiles())){
                    if (f.getName().equals("src")){
                        FileUtils.deleteDirectory(f);
                        FileUtils.forceMkdir(srcDir);
                        break;
                    }
                }
            }

            if (studentProject.listFiles() != null){
                for (File f: Objects.requireNonNull(studentProject.listFiles())){
                    if (f.getName().equals("src")){
                        FileUtils.copyDirectory(f, srcDir);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
