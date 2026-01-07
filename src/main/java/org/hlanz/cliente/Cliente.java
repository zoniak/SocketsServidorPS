package org.hlanz.cliente;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        /*
        El Socket es el extremo de un enlace de comunicación bidireccional. Se utiliza tanto en el cliente como en el servidor.

        En el Cliente: Se crea indicando la dirección IP y el puerto del servidor al que se quiere conectar.
        En el Servidor: Se obtiene automáticamente cuando el ServerSocket acepta una conexión.
        Transmisión de datos: Es el que realmente tiene los métodos getInputStream() y getOutputStream() para enviar y recibir información.
         */
        try (Socket socket = new Socket("localhost", 8080)) {

            PrintWriter salida = new PrintWriter(
                    socket.getOutputStream(), true
            );
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            salida.println("Hola desde el cliente");

            String respuesta = entrada.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}