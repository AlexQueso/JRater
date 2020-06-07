import compiler.Compiler;
import compiler.CompilerAntJava;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import reports.GlobalReportGenerator;
import reports.SingleProjectReportGenerator;
import tester.Tester;
import tester.TesterAntJava;
import unplager.Unplager;
import unplager.UnplagerJplag;

import java.io.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.*;

public class Script {

    public static final String TEMPORAL_PROJECT = "temporal_project";

    private static final Object globalReportLock = new Object();

    /**
     * Compiles, builds and test all the projects located in a specific directory and generates json reports for any of
     * those projects.
     * Also generates a global report.
     */
    public static void rateDirectory(final File referenceProject, File studentProjects, final String practiceName, String pathToJPlag){
        if (studentProjects.toString().contains(".zip")) {
            studentProjects = unzip(studentProjects, studentProjects.getParentFile());
            studentProjects = Objects.requireNonNull(studentProjects.listFiles())[0];
        }
        final File[] studentProjectList = studentProjects.listFiles();
        if (studentProjectList != null){
            int nProjects = studentProjectList.length;
            LinkedList<JSONObject> reportList = new LinkedList<>();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

            for(int i=0; i<nProjects; i++){
                final int finalI = i;
                Callable<String> callable = () -> rateProject(referenceProject, studentProjectList[finalI],
                        true, practiceName, reportList);
                completionService.submit(callable);
            }

            for(int i=0; i<nProjects; i++) {
                try {
                    Future<String> f = completionService.take();
                    System.out.println(i+1 + "/" + nProjects +" - Rated: " + f.get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            }
            shutDownExecutorService(executor);

            JSONObject globalReport = GlobalReportGenerator.generate(reportList);
            saveJSONInDir(globalReport, studentProjects.getParent(), "global_report");
            System.out.println("\nGlobal report JSON saved in " + referenceProject.getParent() + "/global_report.json\n");

            antiplag(studentProjects, referenceProject, pathToJPlag);

            System.out.println("---- RATING COMPLETE ----");
        }
    }

    /**
     * Compiles, builds and test a project made of a reference project and the src code from a student
     * and generates a json report.
     */
    public static String rateProject(File referenceProject, File studentProject, boolean studentNameRelevant,
                                     String practiceName, LinkedList<JSONObject> reportList){
        if (studentProject.toString().contains(".zip"))
            studentProject = unzip(studentProject, studentProject.getParentFile());

        String studentName = "";
        if (studentNameRelevant) {
            studentName = getStudentNameFromDirName(studentProject);
            System.out.println("Rating student project: " + studentName + " ...");
        } else {
            System.out.println("Rating student project: " + studentProject.getName() + " ...");
        }

        File tempDir = createTemporalDir(studentProject);
        fillTemporalProject(tempDir, referenceProject, studentProject);

        Compiler compiler = new CompilerAntJava(tempDir);
        System.out.println(studentName + ": Building project...");
        String buildTrace = compiler.build();

        String testTrace = null;
        if (!buildTrace.contains("FAILED")){
            Tester tester = new TesterAntJava(tempDir);
            System.out.println(studentName + ": Executing tests...");
            testTrace = tester.test(buildTrace);
        } else {
            System.out.println(studentName + ": Test skipped (build failure)");
        }

        //String testTrace = testTraceExample();
        JSONObject json = SingleProjectReportGenerator.generate(buildTrace, testTrace, studentName, practiceName);
        saveJSONInDir(json, studentProject.getPath(), "build_test_report");
        System.out.println(studentName + ": JSON file saved in: " + studentProject + "/build_test_report.json");

        synchronized (globalReportLock) {
            if (reportList != null)
                reportList.add(json);
        }

        deleteDirectory(tempDir);

        return studentName;
    }

    /**
     * Using JPlag, checks the similarities of the projects located in a specific directory
     */
    public static void antiplag(File studentProjects, File referenceProject, String pathToJplag) {
        System.out.println("Checking plagiarism at " + studentProjects.getPath());

        if (studentProjects.toString().contains(".zip")) {
            studentProjects = unzip(studentProjects, studentProjects.getParentFile());
            studentProjects = Objects.requireNonNull(studentProjects.listFiles())[0];
        }

        Unplager unplager = new UnplagerJplag();
        System.out.println(unplager.detect(studentProjects, studentProjects.getParentFile(), pathToJplag));
    }

    /**
     * Creates a temporal directory inside the student project directory
     */
    private static File createTemporalDir(File studentProject){
        //File projects = studentProject.getParentFile();
        if (studentProject.listFiles() != null){
            for (File f: Objects.requireNonNull(studentProject.listFiles())){
                if (f.getName().equals(TEMPORAL_PROJECT)){
                    try {
                        FileUtils.deleteDirectory(f);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        File temporalProjectDir = new File(studentProject, TEMPORAL_PROJECT);
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
    private synchronized static void fillTemporalProject(File temporalDirectory, File referenceProject, File studentProject){
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

    /**
     * Saves a JSONObject instance in the Student project directory
     */
    private static void saveJSONInDir(JSONObject json, String studentDirPath, String fileName){
        try (FileWriter file = new FileWriter(studentDirPath + "/" + fileName +".json")) {
            file.write(json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File unzip(File zipFile, File destination){
        File unzippedFile = null;
        String unzippedFileName = zipFile.getName().replace(".zip", "");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "unzip " + zipFile.getAbsolutePath() + " -d " +
                destination.getAbsolutePath() + "/" + unzippedFileName);
        try {
            Process process = processBuilder.start();
            int exitVal = process.waitFor();
            if (exitVal != 0)
                throw new RuntimeException("Failure unzipping: " + zipFile.getName());

            File[] files = destination.listFiles();
            if (files != null){
                for (File f: files){
                    if (f.getName().equals(unzippedFileName)) {
                        for (File file : f.listFiles()){
                            unzippedFile = f;
                            break;
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Failure unzipping: " + zipFile.getName());
        }
        return unzippedFile;
    }

    private static String getStudentNameFromDirName(File studentProject){
        String name;
        if (studentProject.getName().contains("assignsubmission")){
            String dirName = studentProject.getName();
            int aux = dirName.indexOf("_");
            name = dirName.substring(0, aux) + " ";
            dirName = dirName.substring(aux+1);
            aux = dirName.indexOf("_");
            name += dirName.substring(0, aux);
            dirName = dirName.substring(aux+1);
            aux = dirName.indexOf("_");
            if (!dirName.substring(0, aux).contains("assignsubmission") && !isNumeric(dirName.substring(0, 1)))
                name += " " + dirName.substring(0, aux);
        } else {
            name = studentProject.getName().replace("_", " ");
        }
        return name;
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static void shutDownExecutorService(ExecutorService executor){
        try {
            executor.shutdown();
            boolean success = false;
            success = executor.awaitTermination(3, TimeUnit.SECONDS);
            if (!success)
                executor.shutdownNow();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDirectory(File directory){
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
