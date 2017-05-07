/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.regex.Pattern;
import javax.swing.JFrame;
import static prefuse.demos.AggregateDemo.demo;

/**
 *
 * @author ado_k
 */
public class NewClass {


public static void main(String[] argv) {
        System.out.println(Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)"
            + "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|"
            + "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))"
            + "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*").matcher ("0.0a").matches());
    }
}
