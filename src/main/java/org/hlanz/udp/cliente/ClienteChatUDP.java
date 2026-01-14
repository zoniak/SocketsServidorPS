package org.hlanz.udp.cliente;

import java.net.*;
import java.util.Scanner;

public class ClienteChatUDP {
    private static final String HOST = "localhost";
    private static final int PUERTO_SERVIDOR = 9876;
    public static final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;
    private InetAddress direccionServidor;
    private String nombreUsuario;
    private volatile boolean activo = true;

    public ClienteChatUDP(String nombre) {
        this.nombreUsuario = nombre;
    }

    public void iniciar() {
        try {
            // Crear socket UDP (el sistema asigna un puerto aleatorio)
            socket = new DatagramSocket();
            direccionServidor = InetAddress.getByName(HOST);

            System.out.println("=== CLIENTE CHAT UDP ===");
            System.out.println("Conectando a " + HOST + ":" + PUERTO_SERVIDOR);
            System.out.println("Usuario: " + nombreUsuario);
            System.out.println("Puerto local: " + socket.getLocalPort());
            System.out.println("\nComandos: /usuarios, /salir\n");

            // Enviar mensaje de conexión
            enviarMensaje("CONECTAR:" + nombreUsuario);

            // Thread para recibir mensajes
            Thread receptor = new Thread(new ReceptorMensajes(socket));
            receptor.start();

            // Thread principal para enviar mensajes
            Scanner scanner = new Scanner(System.in);
            while (activo) {
                String mensaje = scanner.nextLine();

                if (mensaje == null || mensaje.trim().isEmpty()) {
                    continue;
                }

                if (mensaje.equalsIgnoreCase("/salir")) {
                    enviarMensaje("DESCONECTAR:" + nombreUsuario);
                    System.out.println("Desconectando...");
                    activo = false;
                    break;
                } else if (mensaje.equalsIgnoreCase("/usuarios")) {
                    enviarMensaje("USUARIOS:" + nombreUsuario);
                } else {
                    enviarMensaje("MENSAJE:" + nombreUsuario + ":" + mensaje);
                }
            }

            scanner.close();
            Thread.sleep(500); // Dar tiempo para recibir últimos mensajes

        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }//fin iniciar

    private void enviarMensaje(String mensaje) {
        try {
            byte[] datos = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(datos, datos.length,
                    direccionServidor,
                    PUERTO_SERVIDOR);
            socket.send(paquete);

        } catch (Exception e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
    }//fin enviarMensaje

    private void cerrarConexion() {
        activo = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Conexión cerrada");
    }//fin cerrarConexion
}//fin clase ClienteChatUDP

