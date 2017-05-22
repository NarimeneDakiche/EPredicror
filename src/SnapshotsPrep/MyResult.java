/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnapshotsPrep;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyResult {

    // etc
        /*public MyResult(int minTS, int maxTS) {
     this.minTS = minTS;
     this.maxTS = maxTS;
     }*/
    private long minTS;
    private long maxTS;

    public long getMinTS() {
        return minTS;
    }

    public long getMaxTS() {
        return maxTS;
    }

    public void setMinTS(long minTS) {
        this.minTS = minTS;
    }

    public void setMaxTS(long maxTS) {
        this.maxTS = maxTS;
    }

    public MyResult getResults(String file, String timeFormat, String dataStructure) throws FileNotFoundException, IOException, ParseException {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            String[] splitContent;
            long timestamp;
            Date d;
            boolean first = true;
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                    splitContent = SnapshotsPrep.splitInput(sCurrentLine);// sCurrentLine.split(separator);
                    if (timeFormat != null && !timeFormat.equals("Timestamp")) {
                        String dateS = "";
                        for (int i = -1; (i = dataStructure.indexOf("T", i + 1)) != -1;) {
                            dateS += splitContent[i];
                        } // prints "4", "13", "22"

                        d = new SimpleDateFormat(timeFormat)
                                .parse(splitContent[0] + " " + splitContent[1]);
                        timestamp = d.getTime();
                    } else {
                        maxTS = Math.max(maxTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));
                        if (first) {
                            minTS = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                            first = false;
                        } else {
                            minTS = Math.min(minTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));
                        }
                    }
                }
            }
        }
        /*String[] splitContent;
         int v, w;
         long timestamp;
         Date d;
         boolean first = true;*/
        /*try (Scanner scanner = new Scanner(stream)) {
         while (scanner.hasNextLine()) {
         splitContent = scanner.nextLine().split(separator);
         if (timeFormat != null && !timeFormat.equals("Timestamp")) {
         String dateS = "";
         for (int i = -1; (i = dataStructure.indexOf("T", i + 1)) != -1;) {
         dateS += splitContent[i];
         } // prints "4", "13", "22"

         d = new SimpleDateFormat(timeFormat)
         .parse(splitContent[0] + " " + splitContent[1]);
         timestamp = d.getTime();
         } else {
         maxTS = Math.max(maxTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));
         if (first) {
         minTS = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
         first = false;
         } else {
         minTS = Math.min(minTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));

         }
         }
         }
         } finally {
         if (stream != null) {
         stream.close();
         }
         }*/

        MyResult myResult = new MyResult();
        myResult.setMaxTS(0);
        myResult.setMinTS(0);
        return myResult;
        // impl here
    }

}
