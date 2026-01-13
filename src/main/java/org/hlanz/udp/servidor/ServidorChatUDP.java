package org.hlanz.udp.servidor;


import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorChatUDP {
    private static final int PUERTO = 9876;
    private static final int BUFFER_SIZE = 1024;

    // Map para almacenar clientes: nombre -> dirección IP y puerto
    private static Map<String, ClienteInfo> clientes = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR CHAT UDP ===");
        System.out.println("Puerto: " + PUERTO);
        System.out.println("Esperando datagramas...\n");

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                // Recibir datagrama
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(paqueteRecibido);

                // Extraer información del paquete
                String mensaje = new String(paqueteRecibido.getData(), 0,
                        paqueteRecibido.getLength()).trim();
                InetAddress direccionCliente = paqueteRecibido.getAddress();
                int puertoCliente = paqueteRecibido.getPort();

                System.out.println("Recibido de " + direccionCliente + ":" +
                        puertoCliente + " -> " + mensaje);

                // Procesar mensaje
                procesarMensaje(mensaje, direccionCliente, puertoCliente, socket);

                // Limpiar buffer
                buffer = new byte[BUFFER_SIZE];
            }

        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void procesarMensaje(String mensaje, InetAddress direccion,
                                        int puerto, DatagramSocket socket) {
        try {
            // Formato de mensaje: COMANDO:USUARIO:CONTENIDO
            String[] partes = mensaje.split(":", 3);

            if (partes.length < 2) return;

            String comando = partes[0];
            String nombreUsuario = partes[1];

            switch (comando) {
                case "CONECTAR":
                    conectarCliente(nombreUsuario, direccion, puerto, socket);
                    break;

                case "MENSAJE":
                    if (partes.length == 3) {
                        String contenido = partes[2];
                        broadcast(nombreUsuario + ": " + contenido,
                                nombreUsuario, socket);
                    }
                    break;

                case "DESCONECTAR":
                    desconectarCliente(nombreUsuario, socket);
                    break;

                case "USUARIOS":
                    enviarListaUsuarios(direccion, puerto, socket);
                    break;

                default:
                    System.out.println("Comando desconocido: " + comando);
            }

        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    private static void conectarCliente(String nombre, InetAddress direccion,
                                        int puerto, DatagramSocket socket) {
        ClienteInfo cliente = new ClienteInfo(direccion, puerto);
        clientes.put(nombre, cliente);

        System.out.println("*** " + nombre + " conectado desde " +
                direccion + ":" + puerto);

        // Confirmar conexión al cliente
        enviarMensaje("SERVIDOR:Bienvenido " + nombre + " al chat UDP!",
                direccion, puerto, socket);

        // Notificar a todos
        broadcast("*** " + nombre + " se ha unido al chat ***", nombre, socket);
    }

    private static void desconectarCliente(String nombre, DatagramSocket socket) {
        if (clientes.remove(nombre) != null) {
            System.out.println("*** " + nombre + " desconectado");
            broadcast("*** " + nombre + " ha salido del chat ***", nombre, socket);
        }
    }

    private static void broadcast(String mensaje, String remitente,
                                  DatagramSocket socket) {
        for (Map.Entry<String, ClienteInfo> entry : clientes.entrySet()) {
            // No enviar al remitente
            if (!entry.getKey().equals(remitente)) {
                ClienteInfo cliente = entry.getValue();
                enviarMensaje(mensaje, cliente.direccion, cliente.puerto, socket);
            }
        }
    }

    private static void enviarListaUsuarios(InetAddress direccion, int puerto,
                                            DatagramSocket socket) {
        StringBuilder lista = new StringBuilder("Usuarios conectados: ");
        for (String nombre : clientes.keySet()) {
            lista.append(nombre).append(", ");
        }
        if (lista.length() > 22) {
            lista.setLength(lista.length() - 2);
        }
        enviarMensaje("SERVIDOR:" + lista.toString(), direccion, puerto, socket);
    }

    private static void enviarMensaje(String mensaje, InetAddress direccion,
                                      int puerto, DatagramSocket socket) {
        try {
            byte[] datos = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(datos, datos.length,
                    direccion, puerto);
            socket.send(paquete);

            System.out.println("Enviado a " + direccion + ":" + puerto +
                    " -> " + mensaje);

        } catch (Exception e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
        }
    }
}