package org.hlanz.chat;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteChat {
    private static final String HOST = "localhost"; //Poner aqui vuestra ip (en windows ipconfig /all)
    private static final int PUERTO = 8080; //Vamos a dejar este puerto al no ser que haya problemas

    //conexion
    private Socket socket;
    //envia datos
    private PrintWriter salida;
    //recibe datos
    private BufferedReader entrada;
    //para consola
    private Scanner scanner;
    //controlar si seguimos que en el chat
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

            //unidad minima de procesamiento para escuchar mensajes del servidor
            Thread listener = new Thread(new ListenerServidor());
            listener.start();

            //hilo principal para enviar mensajes
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
            //para que no se quede nada abierto
            cerrarConexion();
        }
    }//fin metodo principal

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
    }//fin cerrar conexion

    // Clase privada para lanzar hilos que escuchen mensajes del servidor
    private class ListenerServidor implements Runnable {
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
    }//fin clase recibir

    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE CHAT ===");
        ClienteChat cliente = new ClienteChat();
        cliente.iniciar();
    }//fin main
}//fin clase