package org.hlanz.udp.cliente;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa tu nombre de usuario: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            nombre = "Usuario_" + System.currentTimeMillis() % 1000;
            System.out.println("Usando nombre: " + nombre);
        }

        // Validar que el nombre no contenga ':'
        if (nombre.contains(":")) {
            System.out.println("El nombre no puede contener el carácter ':'");
            scanner.close();
            return;
        }

        ClienteChatUDP cliente = new ClienteChatUDP(nombre);
        cliente.iniciar();

        scanner.close();
    }
}
