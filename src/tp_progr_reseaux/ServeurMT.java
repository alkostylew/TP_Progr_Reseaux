package tp_progr_reseaux;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMT extends Thread {
    private boolean isActive = true;
    private int nombreClients = 0;

    public static void main(String[] args) {
        new ServeurMT().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket sc = new ServerSocket(1234);
            while (isActive) {
                Socket socket = sc.accept();
                ++nombreClients;
                new Conversation(socket, nombreClients).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Conversation extends Thread {
        private Socket socketClient;
        private int numero;

        public Conversation(Socket socketClient, int numero) {
            this.socketClient = socketClient;
            this.numero = numero;
        }

        @Override
        public void run() {
            try {
                InputStream is = socketClient.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                br.readLine();

                PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
                String ipClient = socketClient.getRemoteSocketAddress().toString();
                pw.println("Bien venue, vous êtes le client numéro "+numero);
                System.out.println("Connéxion du client numéro "+numero+" IP = "+ipClient);

                while (true) {
                    String req = br.readLine();
                    String response = "Length="+req.length();
                    pw.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
