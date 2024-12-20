package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WebScraperServer
{

    public static void main(String[] args) throws IOException {

        try(ServerSocket serverSocket = new ServerSocket(5000))
        {
            System.out.println("Server is running...");
            System.out.println("--------------------------------------");
            int clientId = 1;
            while (true) {

                try
                {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client with ID" + clientId + " connected!" );
                    System.out.println("--------------------------------------");

                    ClientHandler clientHandler = new ClientHandler(socket, serverSocket, clientId);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    clientId+=1;
                }
                catch (IOException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        }
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

