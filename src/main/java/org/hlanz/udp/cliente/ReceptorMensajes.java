package org.hlanz.udp.cliente;
import java.net.*;

import static org.hlanz.udp.cliente.ClienteChatUDP.BUFFER_SIZE;

// Clase interna para recibir mensajes
public class ReceptorMensajes implements Runnable {
    private volatile boolean activo = true;
    private DatagramSocket socket;

    public ReceptorMensajes(DatagramSocket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (activo) {
            try {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

                // Timeout para poder verificar si activo == false
                socket.setSoTimeout(1000);

                try {
                    socket.receive(paquete);

                    String mensaje = new String(paquete.getData(), 0,
                            paquete.getLength()).trim();

                    // Mostrar mensaje recibido
                    System.out.println(mensaje);

                } catch (SocketTimeoutException e) {
                    // Timeout normal, continuar esperando
                    continue;
                }

                // Limpiar buffer
                buffer = new byte[BUFFER_SIZE];

            } catch (Exception e) {
                if (activo) {
                    System.err.println("Error recibiendo mensaje: " +
                            e.getMessage());
                }
            }
        }
    }
}
