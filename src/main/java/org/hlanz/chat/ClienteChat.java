package org.hlanz.chat;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteChat {
    private static final String HOST = "localhost";
    private static final int PUERTO = 8080;

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Scanner scanner;
    private volatile boolean conectado = true;

    public ClienteChat() {
        scanner = new Scanner(System.in);
    }

    public void iniciar() {
        try {
            socket = new Socket(HOST, PUERTO);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Conectado al servidor de chat");

            // Thread para escuchar mensajes del servidor
            Thread escuchador = new Thread(new EscuchadorServidor());
            escuchador.start();

            // Thread principal para enviar mensajes
            while (conectado) {
                String mensaje = scanner.nextLine();
                if (mensaje != null && !mensaje.trim().isEmpty()) {
                    salida.println(mensaje);

                    if (mensaje.equalsIgnoreCase("/salir")) {
                        conectado = false;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void cerrarConexion() {
        try {
            conectado = false;
            if (scanner != null) scanner.close();
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
            System.out.println("Desconectado del servidor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para escuchar mensajes del servidor
    private class EscuchadorServidor implements Runnable {
        @Override
        public void run() {
            try {
                String mensaje;
                while (conectado && (mensaje = entrada.readLine()) != null) {
                    System.out.println(mensaje);
                }
            } catch (IOException e) {
                if (conectado) {
                    System.err.println("Conexión perdida con el servidor");
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE CHAT ===");
        ClienteChat cliente = new ClienteChat();
        cliente.iniciar();
    }
}