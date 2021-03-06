package com.bsuir.by.Diffie_Hellman;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Bob {
    public static void main(String[] arg) {
        try {
            System.out.println("Contacting Alice....");
            Socket clientSocket = new Socket("127.0.0.1", 2525);
            System.out.println("Contact established....");
            ObjectOutputStream coos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream cois = new ObjectInputStream(clientSocket.getInputStream());
            int[] values = (int[]) cois.readObject();
            int p = values[0], g = values[1], A = values[2], b;
            System.out.println("Received p, g and A values: " + p + ", " + g + ", " + A);
            System.out.println("Generate secret key or input it? 1 - input, else - random.");
            Scanner scanner = new Scanner(System.in);
            char choice = scanner.nextLine().charAt(0);
            switch (choice) {
                case '1':
                    while (true) {
                        System.out.println("Input secret key b:");
                        scanner = new Scanner(System.in);
                        try {
                            b = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Incorrect input!");
                            continue;
                        }
                        if (b <= 0) {
                            System.out.println("b must be positive!");
                            continue;
                        }
                        if (b >= p - 1) {
                            System.out.println("b must be less than p - 1!");
                            continue;
                        }
                        break;
                    }
                    break;
                default:
                    b = ThreadLocalRandom.current().nextInt(10000, 1000000 + 1);
            }
            int B = new BigInteger(Integer.toString(g)).modPow(new BigInteger(Integer.toString(b)), new BigInteger(Integer.toString(p))).intValue();
            coos.writeObject(B);
            System.out.println("Sent B value: " + B);
            int key = new BigInteger(Integer.toString(A)).modPow(new BigInteger(Integer.toString(b)), new BigInteger(Integer.toString(p))).intValue();
            System.out.println("Key received: " + key);
            coos.close();
            cois.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
