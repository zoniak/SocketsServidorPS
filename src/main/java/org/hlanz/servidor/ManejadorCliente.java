package org.hlanz.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ManejadorCliente implements Runnable {
    private Socket clientSocket;

    public ManejadorCliente(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                PrintWriter salida = new PrintWriter(
                        clientSocket.getOutputStream(), true
                )
        ) {
            String direccionCliente = clientSocket.getInetAddress().toString();
            salida.println("Bienvenido al servidor. Escribe 'salir' para desconectar.");

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Cliente " + direccionCliente + ": " + mensaje);

                if (mensaje.equalsIgnoreCase("salir")) {
                    salida.println("Desconectando... ¡Adiós!");
                    break;
                }

                // Procesar el mensaje y responder
                String respuesta = procesarMensaje(mensaje);
                salida.println(respuesta);
            }

            System.out.println("Cliente desconectado: " + direccionCliente);

        } catch (IOException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String procesarMensaje(String mensaje) {
        // Aquí puedes implementar la lógica de tu servidor
        return "Eco: " + mensaje + " [Procesado por thread: " +
                Thread.currentThread().getName() + "]";
    }
}