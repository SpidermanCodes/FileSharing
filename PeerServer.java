import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PeerServer {
    private static final int PORT = 8888;
    private static String sharedFolder;

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and listening on port " + PORT);
            System.out.println("Shareable folder: " + sharedFolder);
            
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected: " + socket.getInetAddress());
                    handleClientRequest(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            String command = dis.readUTF(); // "UPLOAD" or "DOWNLOAD"
            String fileName = dis.readUTF();

            if ("DOWNLOAD".equalsIgnoreCase(command)) {
                sendFile(socket, fileName, dos);
            } else if ("UPLOAD".equalsIgnoreCase(command)) {
                receiveFile(socket, fileName, dis);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(Socket socket, String fileName, DataOutputStream dos) throws IOException {
        File file = new File(sharedFolder, fileName);
        if (file.exists() && !file.isDirectory()) {
            dos.writeUTF("OK");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, bytesRead);
            }
            fis.close();
            System.out.println("File sent: " + fileName);
        } else {
            dos.writeUTF("File not found");
            System.out.println("File not found: " + fileName);
        }
    }

    private static void receiveFile(Socket socket, String fileName, DataInputStream dis) throws IOException {
        File file = new File(sharedFolder, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = dis.read(buffer)) > 0) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        System.out.println("File received: " + fileName);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path of the folder to share: ");
        sharedFolder = scanner.nextLine();

        File folder = new File(sharedFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path. Exiting...");
            return;
        }

        startServer();
    }
}
