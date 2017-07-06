/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvaluationReport;

import java.lang.reflect.Field;

/**
 *
 * @author ado_k
 */
public class Benchmark {

    private String sourceLink;

    private String averageDegree;

    private String maxDegree;

    private String description;

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public void setAverageDegree(String averageDegree) {
        this.averageDegree = averageDegree;
    }

    public void setMaxDegree(String maxDegree) {
        this.maxDegree = maxDegree;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public String getAverageDegree() {
        return averageDegree;
    }

    public String getMaxDegree() {
        return maxDegree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

}
