package org.hlanz.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServidorChat {
    private static final int PUERTO = 8080;
    private static final int MAX_CLIENTES = 10;

    // Lista thread-safe de todos los clientes conectados
    private static Set<ManejadorClienteChat> clientes = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        System.out.println("=== SERVIDOR DE CHAT ===");
        System.out.println("Iniciado en puerto " + PUERTO);
        System.out.println("Esperando clientes...\n");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ManejadorClienteChat manejador = new ManejadorClienteChat(clientSocket);
                clientes.add(manejador);
                pool.execute(manejador);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }//fin main

    // Método para broadcast a todos los clientes
    public static void broadcast(String mensaje, ManejadorClienteChat remitente) {
        for (ManejadorClienteChat cliente : clientes) {
            if (cliente != remitente) {
                cliente.enviarMensaje(mensaje);
            }
        }
    }//fin broadcast

    // Método para remover cliente desconectado
    public static void removerCliente(ManejadorClienteChat cliente) {
        clientes.remove(cliente);
    }//fin removerCliente

    // Método para obtener lista de usuarios conectados
    public static String obtenerListaUsuarios() {
        StringBuilder lista = new StringBuilder("Usuarios conectados: ");
        for (ManejadorClienteChat cliente : clientes) {
            lista.append(cliente.getNombreUsuario()).append(", ");
        }
        if (lista.length() > 22) {
            lista.setLength(lista.length() - 2); // Quitar última coma
        }
        return lista.toString();
    }//fin obtenerUsuarios
}// fin clase
