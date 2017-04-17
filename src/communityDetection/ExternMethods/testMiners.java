/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communityDetection.ExternMethods;

/**
 *
 * @author HADJER
 */
public class testMiners {

    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        //SLPA j=new SLPA();

        //(new SLPA()).findCommunities(".\\LibDetection\\SLPA\\test.ipairs");
        //(new GN()).findCommunities2(".\\LibDetection\\CONGA\\export0.txt",6);
        //(new CONGA()).findCommunities2(".\\LibDetection\\CONGA\\export0.txt",6);
        (new CONCLUDE()).findCommunities2(".\\LibDetection\\CONCLUDE\\export0.txt");
    }
}
