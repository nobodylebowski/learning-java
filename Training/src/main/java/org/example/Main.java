package org.example;

import javax.swing.*;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("First number: ");
        double value1 = Integer.valueOf(scanner.nextLine());
        System.out.println("Second number: ");
        double value2 = Integer.valueOf(scanner.nextLine());
        System.out.println("Operation (+,-,x,/)");
        char op = scanner.next().charAt(0);
        try {
            double result = calculate(value1, value2, op);
            System.out.println(value1 + " " + op + " " + value2 + " = " + result);
        } catch (ArithmeticException | IllegalArgumentException e) {
            System.out.println("Error: " + e);
        }

    }

    static double calculate(double value1, double value2, char op) {
        switch (op) {
            case '+':
                return value1 + value2;
            case '-':
                return value1 - value2;
            case 'x':
                return value1 * value2;
            case '/':
                if (value2 == 0) {
                    throw new ArithmeticException("Cannot divide by 0");
                }
                return value1 / value2;
            default:
                throw new IllegalArgumentException("Wrong operation: " + op);
        }
    }

}