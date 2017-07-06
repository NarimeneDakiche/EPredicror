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
 * 
 * The DetectionUtils contains methods that are needed in the package
 */
public class DetectionUtils {

    /**
     * Extract the filename from a path*
     */
    public static String getfileName(String string) {
        Path p = Paths.get(string);
        String fileName = p.getFileName().toString();
        return (fileName.indexOf(".") >= 0) ? fileName.substring(0, fileName.indexOf(".")) : fileName;
    }
}