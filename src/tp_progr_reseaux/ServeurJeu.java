package tp_progr_reseaux;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeu extends Thread {
    private boolean isActive = true;
    private int nombreClients = 0;
    private int nombreSecret;
    private boolean fin;
    private String gagnant;

    public static void main(String[] args) {
        new ServeurJeu().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket sc = new ServerSocket(1234);
            nombreSecret = new Random().nextInt(1000);
            System.out.println("Le serveur a choisi son nombre secret: "+nombreSecret);
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
                pw.println("Devinez le nombre secret..?");

                while (true) {
                    String req = br.readLine();
                    int nombre = 0;
                    boolean correctFromatRequest = false;
                    try {
                        nombre = Integer.parseInt(req);
                        correctFromatRequest = true;
                    } catch (NumberFormatException e) {
                        pw.println("Format de nombre invalide!");
                        correctFromatRequest = false;
                    }
                    if (correctFromatRequest) {
                        System.out.println("Client "+ipClient+" tentative avec le nombre: "+nombre);
                        if (!fin) {
                            if (nombre > nombreSecret) {
                                pw.println("Votre nombre te supérieur au nombre secret");
                            } else if (nombre < nombreSecret) {
                                pw.println("Votre nombre te indérieur au nombre secret");
                            } else {
                                pw.println("Bravo, vous avez gagné!!");
                                gagnant = ipClient;
                                System.out.println("BRAVO au gagnant, IP client: "+ipClient);
                                fin=true;
                            }
                        } else {
                            pw.println("Le jeu et terminé, le gagnant est: "+gagnant);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
