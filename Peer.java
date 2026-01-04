import java.net.*;
import java.io.*;
import java.util.*;
import java.io.Console;
import java.util.concurrent.atomic.AtomicBoolean;
public class Peer {

    static int port = YOUR_PORT;
    static int peerPort = PEER_PORT;
    static String peerIP = "PEER_IP_ADDRESS";
	static InetAddress otherEndAddress = null;
	static DatagramSocket thisEndSocket = null;
	static DatagramPacket tokenSendingPacket;
	static DatagramPacket tokenReceivingPacket;
	static byte[] token = null;
	static boolean isConnectionEstablished = false;
    static boolean thisEndStartedCommunication = false;
    static BufferedReader reader = null;
    static String message = null;
    static Console console = null;
    static byte[] incomingMessage = null;
    static byte[] outgoingMessage = null;
	static DatagramPacket outgoingMessagePacket = null;
    static DatagramPacket incomingMessagePacket = null;

	public static void shakeHand() {
		token = new byte[1];
		boolean isTokenSent = false;
		boolean isTokenReceived = false;
		try {
			otherEndAddress = InetAddress.getByName(peerIP);
			tokenSendingPacket = new DatagramPacket(token, token.length, otherEndAddress, peerPort);
			tokenReceivingPacket = new DatagramPacket(token, token.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		while(!isConnectionEstablished) {
			try{
				thisEndSocket.send(tokenSendingPacket);

				thisEndSocket.receive(tokenReceivingPacket);
				isTokenReceived = true;

				thisEndSocket.send(tokenSendingPacket);
				isTokenSent = true;

				isConnectionEstablished = true;

                clearConsole();
				System.out.print("Connection established successfully!\n");
					
			} catch(Exception e) {
				System.out.println("Unable to establish connection: Retrying...");
			}
		}
	}

    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if(os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "cls");
            } else {
                processBuilder = new ProcessBuilder("clear");
            }
            processBuilder.inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
    public static void createSocket() {
        try {
			thisEndSocket = new DatagramSocket(port);
			thisEndSocket.setSoTimeout(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static void determineInitiator() {
        try {
            thisEndSocket.setSoTimeout(500);
            thisEndSocket.receive(tokenReceivingPacket);
            thisEndStartedCommunication = true;
            System.out.print("(^-^) Start messaging (^-^)\n\n");
        } catch (Exception ignored) {
            System.out.print("(^-^) Waiting for messages (^-^)\n\n");
        }
        try {
            thisEndSocket.setSoTimeout(0);
        } catch(Exception e) {

        }
    }

    public static void initializeResources() {
        incomingMessage = new byte[1024];
        outgoingMessage = new byte[1024];
        reader = new BufferedReader(new InputStreamReader(System.in));
        console = System.console();
    }

    public static void sendMessage() {
        System.out.print("You: ");
        try{
            message = reader.readLine();
            outgoingMessage = message.getBytes();
            outgoingMessagePacket = new DatagramPacket(outgoingMessage, outgoingMessage.length, otherEndAddress, peerPort);
            thisEndSocket.send(outgoingMessagePacket);
        } catch(Exception e) {

        }
    }

    public static void receiveMessage() {
        try{
            incomingMessagePacket = new DatagramPacket(incomingMessage, incomingMessage.length);
            thisEndSocket.receive(incomingMessagePacket);
        } catch(Exception e) {

        }
        message = new String(incomingMessagePacket.getData(), 0, incomingMessagePacket.getLength());
        System.out.print("Correspondent: ");
        System.out.println(message);
    }

	public static void main(String args[]){
        boolean connectionStatus = true;
		createSocket();
        shakeHand();
        determineInitiator();
        initializeResources();
		do{
            if(thisEndStartedCommunication) {
                sendMessage();
                receiveMessage();
            } else {
                receiveMessage();
                System.out.print("\n");
                sendMessage();
            }
            System.out.print("\n\n");
		}while(connectionStatus);
	}
}