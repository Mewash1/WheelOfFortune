package pap.z26.wheeloffortune;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for communication between game clients and the game server
 */
public class NetworkClient extends Thread {

    private InetAddress serverAddress;
    private DatagramSocket socket;
    private GameServer server;
    private WheelOfFortune game;
    private int port;
    private boolean outputLogs = false;

    /**
     * {@link WheelOfFortune Game client's} NetworkClient constructor
     *
     * @param serverIpAddress IP address of the server that the client will instantly try to connect to
     * @param serverPort      port of the server to connect to
     * @param game            {@link WheelOfFortune game client's} instance that created this NetworkClient, used to pass data
     *                        received from the server
     */
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

    /**
     * {@link GameServer game server's} NetworkClient constructor
     *
     * @param listenPort port on which the server's NetworkClient will listen on for incoming requests
     * @param server     {@link GameServer game server's} instance that created this NetworkClient, used to pass data
     *                   received from a game client
     */
    public NetworkClient(int listenPort, GameServer server) {
        this.server = server;
        this.port = listenPort;
        try {
            this.socket = new DatagramSocket(listenPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs an infinite loop of waiting for a packet and passing the received data to a game client or a game server
     */
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
                    if (outputLogs)
                        System.out.println("[CLIENT |  " + packet.getAddress().toString() + ":" + port + "]: " + message);
                    game.receiveDataFromServer(message);
                } else { // server mode
                    if (outputLogs)
                        System.out.println("[SERVER |  " + packet.getAddress().toString() + ":" + port + "]: " + message);
                    server.receiveDataFromClient(message, packet.getAddress(), packet.getPort());
                }
            }
        }
    }

    /**
     * {@link WheelOfFortune Game client's} method for sending data to the server
     *
     * @param data JSON with data containing a request or information about a move made by a player
     */
    public void sendData(String data) {
        this.sendData(data, serverAddress, port);
    }

    /**
     * {@link GameServer Game server's} method for sending data to clients
     *
     * @param data          JSON with data containing an order to execute a move or with a response to a client's request
     * @param targetAddress IP address of the client
     * @param targetPort    port of the client
     */
    public void sendData(String data, InetAddress targetAddress, int targetPort) {
        DatagramPacket packet = new DatagramPacket(data.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8).length, targetAddress, targetPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a new server address in a {@link WheelOfFortune game client's} NetworkClient
     *
     * @param ipAddress IP address of the server
     * @param port      port on which the server listens
     * @return true if the settings was successful, false otherwise (due to bad IP address)
     */
    public boolean setServerAddress(String ipAddress, int port) {
        try {
            this.serverAddress = InetAddress.getByName(ipAddress);
            this.port = port;
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public void showLogs() {
        outputLogs = true;
    }
}
