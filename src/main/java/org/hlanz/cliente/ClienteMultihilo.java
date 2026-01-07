package org.hlanz.cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteMultihilo {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 8080);
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado al servidor");

            // Leer mensaje de bienvenida
            System.out.println("Servidor: " + entrada.readLine());

            String mensaje;
            while (true) {
                System.out.print("Tú: ");
                mensaje = scanner.nextLine();
                salida.println(mensaje);

                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Servidor: " + entrada.readLine());
                    break;
                }

                String respuesta = entrada.readLine();
                System.out.println("Servidor: " + respuesta);
            }

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}