package reports;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BuildTestReportGenerator {

    public static JSONObject generate(String buildTrace, String testTrace) {
        JSONObject json = new JSONObject();

        if (buildTrace.contains("FAILED")) {
            json.put("build", buildTrace);
            json.put("test", "No se han ejecutado los tests");
        } else {
            json.put("build", buildTrace);
            json.put("test", addTestToJSON(testTrace));
        }
        return json;
    }

    private static JSONArray addTestToJSON(String testTrace) {
        testTrace = testTrace.replace("    [junit] ", "");
        String[] lines = testTrace.split(System.getProperty("line.separator"));
        int linesLength = lines.length;
        int i = 0;
        JSONArray testList = new JSONArray();

        while (i < linesLength) {
            JSONObject testSuite = new JSONObject();
            // "testsuite":"material.maps.HashTableMapLPTes"
            String line = lines[i];
            line = line.replace("Testsuite: ", "");
            testSuite.put("testsuite", line);
            i++;

            // "correct":0, "total":1
            line = lines[i];
            int total, correct, failures;
            line = line.replace("Tests run: ", "");
            line = line.replace(" Failures: ", "");
            line = line.replace(" Errors: ", "");
            line = line.replace(" Skipped: ", "");
            total = Integer.parseInt(line.substring(0, 1));
            line = line.substring(2);
            failures = Integer.parseInt(line.substring(0, 1));
            line = line.substring(2);
            failures += Integer.parseInt(line.substring(0, 1));
            line = line.substring(2);
            failures += Integer.parseInt(line.substring(0, 1));
            correct = total - failures;
            testSuite.put("correct", correct);
            testSuite.put("total", total);
            i++;

            if (i < linesLength) {
                //"testcases" : ...
                if (lines[i].contains("Testsuite"))
                    testSuite.put("testcases", null);
                else {
                    JSONArray testcases = new JSONArray();

                    while (lines[i].contains("Testcase")) {
                        JSONObject testcase = new JSONObject();
                        line = lines[i];
                        line = line.replace("Testcase: ", "");
                        int firstParentheseIndex = line.indexOf("(");
                        String testName = line.substring(0, firstParentheseIndex);
                        int tabAppearenceIndex = line.indexOf("\t");
                        line = line.substring(tabAppearenceIndex + 1);

                        testcase.put("testname", testName);
                        testcase.put("cause", line);

                        i++;
                        StringBuilder trace = new StringBuilder();
                        while (!lines[i].contains("Testsuite") && !lines[i].contains("Testcase")) {
                            line = lines[i];
                            trace.append(line);
                            i++;
                            if (i >= linesLength)
                                break;
                        }

                        testcase.put("trace", trace.toString());
                        testcases.add(testcase);
                        if (i >= linesLength)
                            break;
                    }
                    testSuite.put("testcases", testcases);
                }
            }
            testList.add(testSuite);
        }
        return testList;
    }
}























