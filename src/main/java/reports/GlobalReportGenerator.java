package reports;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;

public class GlobalReportGenerator {

    public static JSONObject generate(LinkedList<JSONObject> reportList) {
        JSONObject report = new JSONObject();
        if (!reportList.isEmpty()){
            report.put("projectName", reportList.get(0).get("projectName"));
            report.put("date", SingleProjectReportGenerator.getDateAndTime());
            report.put("students", students(reportList));
        } else {
            throw new RuntimeException("Failure while generating student reports");
        }
        return report;
    }

    private static JSONArray students(LinkedList<JSONObject> reportList){
        JSONArray students = new JSONArray();
        for (JSONObject report: reportList){
            JSONObject student = new JSONObject();

            student.put("studentName", report.get("studentName"));
            student.put("build", report.get("build"));
            JSONArray tests = new JSONArray();

            for (JSONObject testSuite : (Iterable<JSONObject>) report.get("test")) {
                JSONObject test = new JSONObject();
                test.put("testSuite", testSuite.get("testSuite"));
                test.put("correct", testSuite.get("correct"));
                test.put("total", testSuite.get("total"));
                tests.add(test);
            }
            student.put("test", tests);
            students.add(student);
        }
        return students;
    }
}




















