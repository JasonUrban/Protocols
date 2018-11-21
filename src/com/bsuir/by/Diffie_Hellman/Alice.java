package com.bsuir.by.Diffie_Hellman;

import com.bsuir.by.Server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static com.bsuir.by.Server.isPrime;

public class Alice {
    public static void main(String[] arg) {
        ServerSocket serverSocket = null;
        Socket clientAccepted = null;
        ObjectInputStream sois = null;
        ObjectOutputStream soos = null;
        int p, g, a;
        System.out.println("Generate random parameters or input them? 1 - input, else - random.");
        Scanner scanner = new Scanner(System.in);
        char choice = scanner.nextLine().charAt(0);
        switch (choice) {
            case '1':
                while (true) {
                    System.out.println("Input parameters p, g and secret key a:");
                    scanner = new Scanner(System.in);
                    try {
                        p = scanner.nextInt();
                        g = scanner.nextInt();
                        a = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Incorrect input!");
                        continue;
                    }
                    if (p <= 0 || a <= 0 || g <= 0) {
                        System.out.println("p, a and g must be positive!");
                        continue;
                    }
                    if (a >= p - 1 || g >= p - 1) {
                        System.out.println("a and g must be less than p - 1!");
                        continue;
                    }
                    if (isPrime(p) && isPrime(g) && isPrime((p - 1) / 2)) {
                        int temp = new BigInteger(Integer.toString(g)).modPow(new BigInteger(Integer.toString(p - 1)), new BigInteger(Integer.toString(p))).intValue();
                        if (temp != 1 % p) {
                            System.out.println("Wrong parameter g! It must be primitive root modulo p!");
                            continue;
                        }
                        break;
                    } else {
                        System.out.println("p, (p - 1) / 2 and g must be prime numbers!");
                    }
                }
                break;
            default:
                int temp;
                do {
                    p = ThreadLocalRandom.current().nextInt(2, 1000000 + 1);
                    g = ThreadLocalRandom.current().nextInt(2, (p - 1) + 1);
                    a = ThreadLocalRandom.current().nextInt(10000, 1000000 + 1);
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
            int A = new BigInteger(Integer.toString(g)).modPow(new BigInteger(Integer.toString(a)), new BigInteger(Integer.toString(p))).intValue();
            soos.writeObject(new int[]{p, g, A});
            System.out.println("Sent p, g and A values: " + p + ", " + g + ", " + A);
            int B = (int) sois.readObject();
            System.out.println("Received B: " + B);
            int key = new BigInteger(Integer.toString(B)).modPow(new BigInteger(Integer.toString(a)), new BigInteger(Integer.toString(p))).intValue();
            System.out.println("Key received: " + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Server.closeConnection(serverSocket, clientAccepted, sois, soos);
        }
    }
}
