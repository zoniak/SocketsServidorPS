package org.hlanz.servidor;

import java.io.*;
import java.net.*;

//Ejemplo de cliente que solo puede manejar un hilo (es decir, un unico cliente)
public class Servidor {
    public static void main(String[] args) {

        //Su única misión es escuchar en un puerto específico
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Servidor iniciado en puerto 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado");

                // Streams para comunicación || Lee los archivos por lineas
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                //PrintWriter es como System.out.println() pero dirigido a un destino
                PrintWriter salida = new PrintWriter(
                        clientSocket.getOutputStream(), true
                );

                String mensaje = entrada.readLine();
                System.out.println("Recibido: " + mensaje);

                salida.println("Eco: " + mensaje);

                clientSocket.close();
            }
        } catch (IOException e) { //considerar como conexiones perdidas
            e.printStackTrace();
        }
    }
}