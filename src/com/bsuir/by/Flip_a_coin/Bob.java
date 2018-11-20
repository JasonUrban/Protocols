package com.bsuir.by.Flip_a_coin;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Bob {
    public static void main(String[] arg) {
        try {
            System.out.println("Contacting Alice....");
            Socket clientSocket = new Socket("127.0.0.1", 2525);
            System.out.println("Contact established....");
            ObjectOutputStream coos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream cois = new ObjectInputStream(clientSocket.getInputStream());
            int[] values = (int[]) cois.readObject();
            int p = values[0], g = values[1], y = values[2], k;
            boolean b;
            System.out.println("Received p, g and y values: " + p + ", " + g + ", " + y);
            System.out.println("Generate bit b and number k or input them? 1 - input, else - random.");
            Scanner scanner = new Scanner(System.in);
            char choice = scanner.nextLine().charAt(0);
            switch (choice) {
                case '1':
                    while (true) {
                        System.out.println("Input bit b and number k:");
                        scanner = new Scanner(System.in);
                        try {
                            b = scanner.nextBoolean();
                            k = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Incorrect input!");
                            continue;
                        }
                        if (k <= 0) {
                            System.out.println("k must be positive!");
                            continue;
                        }
                        break;
                    }
                    break;
                default:
                    SecureRandom random = new SecureRandom();
                    b = random.nextBoolean();
                    k = random.nextInt(1000000);
            }
            int r = new BigInteger(Integer.toString(b ? y : 1)).multiply(new BigInteger(Integer.toString(g)).pow(k)).mod(new BigInteger(Integer.toString(p))).intValue();
            coos.writeObject(r);
            System.out.println("Sent r value: " + r);
            boolean c = (boolean) cois.readObject();
            System.out.println("Received bit c: " + c);
            coos.writeObject(new Object[]{b, k});
            System.out.println("Sent b and k values: " + b + ", " + k);
            coos.close();
            cois.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
