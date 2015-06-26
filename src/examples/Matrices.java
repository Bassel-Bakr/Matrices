/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 *
 * @author Bassel Bakr
 */
public class Matrices
{

    private static UncaughtExceptionHandler uncaughtHandler;
    final static StartPage ui = new StartPage()
    {
        {
            setTitle(Matrices.class.getSimpleName());
            //setAlwaysOnTop(true);
            show();
        }
    };
    final static JButton print = ui.printBtn,
            calculate = ui.calculateBtn,
            transpose = ui.transposeBtn,
            inverse = ui.inverseBtn,
            adjoint = ui.solveBtn;
    final static JTextArea pane = ui.editingArea,
            textField = ui.jTextArea1;

    public static void main(String[] args) throws Exception
    {
        uncaughtHandler = new UncaughtExceptionHandler()
        {

            public void uncaughtException(Thread t, Throwable e)
            {
                textField.append(e.getMessage()+"\n\n");
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(uncaughtHandler);
        textField.setAutoscrolls(true);
        print.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Matrix x = new Matrix(pane.getText());
                textField.append("\n===================================\n\n\n");
                textField.append(x.toString());
            }
        });

        calculate.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Matrix x = new Matrix(pane.getText());
                textField.append("\n===================================\n\n\n");
                textField.append(Matrix.toFraction(x.solve(x))+"\n\n");
            }
        });

        transpose.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Matrix x = new Matrix(pane.getText());
                textField.append("\n===================================\n\n\n");
                textField.append(x.transpose(x).toString());
            }
        });

        inverse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Matrix x = new Matrix(pane.getText());
                System.out.println(Arrays.toString(x.getRow(1)));
                textField.append("\n===================================\n\n\n");
                x.solveSteps(x, textField);
            }
        });

        adjoint.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Matrix x = new Matrix(pane.getText());
                textField.append("\n===================================\n\n\n");
                x.solveSteps(x, textField);
            }
        });
    }
}
