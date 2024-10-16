import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PeerClient {
    private static final int PORT = 8888;

    public static void downloadFile(String host, String fileName, String downloadPath) {
        try (Socket socket = new Socket(host, PORT)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF("DOWNLOAD");
            dos.writeUTF(fileName);

            String serverResponse = dis.readUTF();
            if ("OK".equalsIgnoreCase(serverResponse)) {
                FileOutputStream fos = new FileOutputStream(downloadPath + "/" + fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
                System.out.println("File downloaded: " + fileName);
            } else {
                System.out.println(serverResponse); // "File not found"
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile(String host, String filePath) {
        try (Socket socket = new Socket(host, PORT)) {
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("File not found or it's a directory.");
                return;
            }

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF("UPLOAD");
            dos.writeUTF(file.getName());

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, bytesRead);
            }
            fis.close();
            System.out.println("File uploaded: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter peer IP to connect: ");
        String peerHost = scanner.nextLine();

        System.out.println("1. Download a file");
        System.out.println("2. Upload a file");
        System.out.print("Select an option: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        if (option == 1) {
            System.out.print("Enter file name to download: ");
            String fileName = scanner.nextLine();
            System.out.print("Enter download folder path: ");
            String downloadPath = scanner.nextLine();
            downloadFile(peerHost, fileName, downloadPath);
        } else if (option == 2) {
            System.out.print("Enter file path to upload: ");
            String filePath = scanner.nextLine();
            uploadFile(peerHost, filePath);
        } else {
            System.out.println("Invalid option.");
        }
    }
}
