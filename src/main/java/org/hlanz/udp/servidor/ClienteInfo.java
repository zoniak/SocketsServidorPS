package org.hlanz.udp.servidor;

import java.net.InetAddress;

// Clase para almacenar información del cliente
class ClienteInfo {
    InetAddress direccion;
    int puerto;

    public ClienteInfo(InetAddress direccion, int puerto) {
        this.direccion = direccion;
        this.puerto = puerto;
    }
}