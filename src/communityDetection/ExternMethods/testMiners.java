/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection.ExternMethods;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author HADJER
 */
public class testMiners {

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        //SLPA j=new SLPA();

        //(new SLPA()).findCommunities(".\\LibDetection\\SLPA\\test.ipairs");

        String string = ".\\LibDetection\\SLPA\\test.parse";
       
        
        System.out.println(fileName(string));
        //(new GN()).findCommunities(".\\LibDetection\\CONGA\\dolphins-edges.txt");
        //(new CONGA()).findCommunities(".\\LibDetection\\CONGA\\export0.txt");
    }

    private static String fileName(String string) {
        Path p = Paths.get(string);
        String fileName = p.getFileName().toString();
        return (fileName.indexOf(".")>=0)? fileName.substring(0, fileName.indexOf(".")) : fileName;
    }
}
