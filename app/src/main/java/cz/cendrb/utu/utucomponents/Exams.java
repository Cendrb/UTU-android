package cz.cendrb.utu.utucomponents;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.cendrb.utu.R;

public class Exams {
    public List<Exam> exams;
    public static String[] from;
    public static int[] to;

    public Exams(List<Exam> exams) {
        this.exams = exams;
        setFromAndTo();
    }

    public Exams() {
        setFromAndTo();
        exams = new ArrayList<Exam>();
    }

    public void clearAndLoad(Element inExams) {
        this.exams.clear();
        for (int counter = inExams.getChildNodes().getLength() - 1; counter > 0; counter--) {
            Node node = inExams.getChildNodes().item(counter);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Exam exam = new Exam((Element) node);
                exams.add(exam);
            }
        }
    }

    public Exam findExamWithId(int id) {
        for (Exam exam : exams) {
            if(exam.getId() == id)
                return exam;
        }
        return null;
    }

    private void setFromAndTo() {
        from = new String[]{Exam.TITLE, Exam.DESCRIPTION, Exam.SUBJECT,
                Exam.DATE, Exam.GROUP};
        to = new int[]{R.id.examTitle, R.id.examDescription, R.id.examSubject, R.id.examDatum,
                R.id.examGroup};
    }

    public List<HashMap<String, String>> getListForAdapter() {
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        for (Exam exam : exams) {
            data.add(exam.getRecord());
        }

        return data;
    }
}
