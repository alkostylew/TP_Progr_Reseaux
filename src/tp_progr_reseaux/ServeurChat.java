package tp_progr_reseaux;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServeurChat extends Thread {
    private boolean isActive = true;
    private int nombreClients = 0;
    private List<Conversation> clients = new ArrayList<>();

    public static void main(String[] args) {
        new ServeurChat().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket sc = new ServerSocket(1234);
            while (isActive) {
                Socket socket = sc.accept();
                ++nombreClients;
                Conversation conversation = new Conversation(socket, nombreClients);
                clients.add(conversation);
                conversation.start();
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

        private void broadcastMessage(String message, Socket socket, int numeroClient) {
            try {
                for (Conversation client: clients) {
                    if (client.socketClient != socket) {
                        if (client.numero == numeroClient || numeroClient == -1) {
                            PrintWriter pw = new PrintWriter(client.socketClient.getOutputStream(), true);
                            pw.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                InputStream is = socketClient.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
                String ipClient = socketClient.getRemoteSocketAddress().toString();
                pw.println("Bien venue, vous êtes le client numéro "+numero);
                System.out.println("Connéxion du client numéro "+numero+" IP = "+ipClient);

                while (true) {
                    String req = br.readLine();
                    if (req.contains("=>")) {
                        String[] reqParams = req.split("=>");
                        if (reqParams.length == 2) {
                            String message = reqParams[1];
                            int numeroClient = Integer.parseInt(reqParams[0]);
                            broadcastMessage(req, socketClient, numeroClient);
                        }
                    } else {
                        broadcastMessage(req, socketClient, -1);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
