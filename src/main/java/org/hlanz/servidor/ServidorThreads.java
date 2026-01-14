package org.hlanz.servidor;

import java.io.*;
import java.net.*;

public class ServidorThreads {
    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Crear un nuevo thread para cada cliente
                Thread clientThread = new Thread(new ManejadorCliente(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}