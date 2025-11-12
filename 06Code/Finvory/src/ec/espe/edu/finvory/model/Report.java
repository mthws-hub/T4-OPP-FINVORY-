package ec.espe.edu.finvory.model;

import java.util.ArrayList;

/**
 *
 * @author @author Arelys Otavalo, The POOwer Rangers Of Programming
 */
public class Report {
    private String title;
    private ArrayList<String> dataLines = new ArrayList<>();

    public Report(String title) {
        this.title = title;
    }
    
    public void addLine(String line) {
        dataLines.add(line);
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public ArrayList<String> getDataLines() { 
        return dataLines; 
    }
}
