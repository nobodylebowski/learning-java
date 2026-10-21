package org.example;
import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("First number: ");
        int value1 = Integer.valueOf(scanner.nextLine());
        System.out.println("Second number: ");
        int value2 = Integer.valueOf(scanner.nextLine());
        System.out.println(value1 + " + " + value2 + " = " + (value1+value2));

    }
}