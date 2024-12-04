package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String clientMessage;
            while ((clientMessage = bf.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    pr.println("exit");
                    break;
                } else if (clientMessage.equalsIgnoreCase("admin")) {
                    System.out.println("Admin shutting down the server.");
                    pr.println("admin");
                    System.exit(0); // Спира целия сървър
                } else {
                    int numberReceived = Integer.parseInt(clientMessage);
                    String result = WebScraperServer.scraping(numberReceived);
                    pr.println(result);
                    pr.println("EndOfSending");
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error with client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}