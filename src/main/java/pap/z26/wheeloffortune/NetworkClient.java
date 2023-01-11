package pap.z26.wheeloffortune;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class NetworkClient extends Thread {

    private InetAddress serverAddress;
    private DatagramSocket socket;
    private GameServer server;
    private WheelOfFortune game;
    private int port;

    public NetworkClient(String serverIpAddress, int serverPort, WheelOfFortune game) {
        this.game = game;
        this.port = serverPort;
        try {
            this.serverAddress = InetAddress.getByName(serverIpAddress);
            this.socket = new DatagramSocket();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    public NetworkClient(int listenPort, GameServer server) {
        this.server = server;
        this.port = listenPort;
        try {
            this.socket = new DatagramSocket(listenPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            byte[] data = new byte[8192];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message = (new String(packet.getData(), StandardCharsets.UTF_8)).replaceAll("\\x00*", "");
            if (!message.isEmpty()) {
                if (game != null) {
                    System.out.println("[CLIENT |  " + packet.getAddress().toString() + ":" + port + "]: " + message);
                    game.receiveDataFromServer(message);
                } else { // server mode
                    System.out.println("[SERVER |  " + packet.getAddress().toString() + ":" + port + "]: " + message);
                    server.receiveDataFromClient(message, packet.getAddress(), packet.getPort());
                }
            }
        }
    }

    public void sendData(String data) {
        this.sendData(data, serverAddress, port);
    }

    public void sendData(String data, InetAddress targetAddress, int targetPort) {
        DatagramPacket packet = new DatagramPacket(data.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8).length, targetAddress, targetPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setServerAddress(String ipAddress, int port) {
        try {
            this.serverAddress = InetAddress.getByName(ipAddress);
            this.port = port;
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
