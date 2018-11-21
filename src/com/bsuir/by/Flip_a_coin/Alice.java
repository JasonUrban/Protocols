package com.bsuir.by.Flip_a_coin;

import com.bsuir.by.Server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.InputMismatchException;
import java.util.Scanner;

import static com.bsuir.by.Server.isPrime;

public class Alice {
    public static void main(String[] arg) {
        ServerSocket serverSocket = null;
        Socket clientAccepted = null;
        ObjectInputStream sois = null;
        ObjectOutputStream soos = null;
        int p, g, x;
        System.out.println("Generate random parameters or input them? 1 - input, else - random.");
        Scanner scanner = new Scanner(System.in);
        char choice = scanner.nextLine().charAt(0);
        switch (choice) {
            case '1':
                while (true) {
                    System.out.println("Input parameters p, g, x:");
                    scanner = new Scanner(System.in);
                    try {
                        p = scanner.nextInt();
                        g = scanner.nextInt();
                        x = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Incorrect input!");
                        continue;
                    }
                    int temp = new BigInteger(Integer.toString(g)).modPow(new BigInteger(Integer.toString(p - 1)), new BigInteger(Integer.toString(p))).intValue();
                    if (temp != 1 % p) {
                        System.out.println("Wrong parameter g! It must be primitive root modulo p!");
                        continue;
                    }
                    if (p <= 0 || x <= 0 || g <= 0) {
                        System.out.println("p, x and g must be positive!");
                        continue;
                    }
                    if (g >= p - 1) {
                        System.out.println("g must be less than p - 1!");
                        continue;
                    }
                    if (isPrime(p) && isPrime(g) && isPrime((p - 1) / 2)) {
                        break;
                    } else {
                        System.out.println("p, (p - 1) / 2 and g must be prime numbers!");
                    }
                }
                break;
            default:
                int temp;
                do {
                    SecureRandom random = new SecureRandom();
                    p = random.nextInt(1000000) + 2;
                    g = random.nextInt(p - 1);
                    x = random.nextInt(1000000);
                    temp = new BigInteger(Integer.toString(g)).modPow(new BigInteger(Integer.toString(p - 1)), new BigInteger(Integer.toString(p))).intValue();
                } while (!isPrime(p) || !isPrime(g) || !isPrime((p - 1) / 2) || temp != 1 % p);
        }
        try {
            System.out.println("Alice listening....");
            serverSocket = new ServerSocket(2525);
            clientAccepted = serverSocket.accept();
            System.out.println("Contact established....");
            sois = new ObjectInputStream(clientAccepted.getInputStream());
            soos = new ObjectOutputStream(clientAccepted.getOutputStream());
            int y = new BigInteger(Integer.toString(g)).modPow(new BigInteger(Integer.toString(x)), new BigInteger(Integer.toString(p))).intValue();
            soos.writeObject(new int[]{p, g, y});
            System.out.println("Sent p, g and y values: " + p + ", " + g + ", " + y);
            int r = (int) sois.readObject();
            System.out.println("Received r: " + r);
            boolean c;
            System.out.println("Generate bit c or input it? 1 - input, else - random.");
            scanner = new Scanner(System.in);
            choice = scanner.nextLine().charAt(0);
            switch (choice) {
                case '1':
                    while (true) {
                        System.out.println("Input bit c:");
                        scanner = new Scanner(System.in);
                        try {
                            c = scanner.nextBoolean();
                        } catch (InputMismatchException e) {
                            System.out.println("Incorrect input!");
                            continue;
                        }
                        break;
                    }
                    break;
                default:
                    SecureRandom random = new SecureRandom();
                    c = random.nextBoolean();
            }
            soos.writeObject(c);
            System.out.println("Sent c value: " + c);
            Object[] values = (Object[]) sois.readObject();
            boolean b = (boolean) values[0];
            int k = (int) values[1];
            if (r == new BigInteger(Integer.toString(b ? y : 1)).multiply(new BigInteger(Integer.toString(g)).pow(k)).mod(new BigInteger(Integer.toString(p))).intValue()) {
                System.out.println("Result is " + (b ^ c ? "heads" : "tails"));
            } else {
                System.out.println("Something went wrong! Try once again...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Server.closeConnection(serverSocket, clientAccepted, sois, soos);
        }
    }
}
