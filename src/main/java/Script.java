import compiler.Compiler;
import compiler.CompilerAntJava;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import reports.BuildTestReportGenerator;
import tester.Tester;
import tester.TesterAntJava;

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

        Tester tester = new TesterAntJava(tempDir);
        String testTrace = tester.test(buildTrace);
        //String testTrace = testTraceExample();
        JSONObject json = BuildTestReportGenerator.generate(buildTrace, testTrace);
        saveJSONInStudentDir(json);
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

    private static void saveJSONInStudentDir(JSONObject json){

    }

    private static String testTraceExample (){
        return "    [junit] Testsuite: material.maps.HashTableMapLPTest\n" +
                "    [junit] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.086 sec\n" +
                "    [junit] Testcase: testBucketSize(material.maps.HashTableMapLPTest):\tFAILED\n" +
                "    [junit] junit.framework.AssertionFailedError\n" +
                "    [junit] \tat material.maps.HashTableMapLPTest.testBucketSize(HashTableMapLPTest.java:37)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "    [junit] Testsuite: practica2.CollisionAnalyzerTest\n" +
                "    [junit] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 13.931 sec\n" +
                "    [junit] Testsuite: practica2.NewsAnalyzerTest\n" +
                "    [junit] Tests run: 2, Failures: 0, Errors: 2, Skipped: 0, Time elapsed: 0.909 sec\n" +
                "    [junit] Testcase: testGetNewsWith(practica2.NewsAnalyzerTest):\tCaused an ERROR\n" +
                "    [junit] java.lang.RuntimeException: Not implemented yet\n" +
                "    [junit] \tat practica2.NewsAnalyzer.getNewsWith(NewsAnalyzer.java:35)\n" +
                "    [junit] \tat practica2.NewsAnalyzerTest.testGetNewsWith(NewsAnalyzerTest.java:29)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "    [junit] Testcase: testMoreFrecuentNamedEntity(practica2.NewsAnalyzerTest):\tCaused an ERROR\n" +
                "    [junit] java.lang.RuntimeException: Not implemented yet\n" +
                "    [junit] \tat practica2.NewsAnalyzer.moreFrecuentNamedEntity(NewsAnalyzer.java:43)\n" +
                "    [junit] \tat practica2.NewsAnalyzerTest.testMoreFrecuentNamedEntity(NewsAnalyzerTest.java:41)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
                "    [junit] \tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n";
    }
}
