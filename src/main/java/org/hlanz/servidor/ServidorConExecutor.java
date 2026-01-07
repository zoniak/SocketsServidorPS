package org.hlanz.servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorConExecutor {
    private static final int PUERTO = 8080;
    private static final int MAX_CLIENTES = 10;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor multihilo iniciado en puerto " + PUERTO);
            System.out.println("Esperando clientes...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress());

                // Asignar el cliente a un thread del pool
                pool.execute(new ManejadorCliente(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }//fin main
}//fin clase

