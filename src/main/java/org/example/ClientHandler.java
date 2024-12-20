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

class ClientHandler implements Runnable  {

    private int id;
    private Socket socket;
    private ServerSocket serverSocket;
    public ClientHandler(Socket socket, ServerSocket serverSocket, int id) {
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {
        try (
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);

        ) {


            while (true) {
                try {
                    String clientMessage = bf.readLine();
                    if (clientMessage == null || clientMessage.equals("exit")) {
                        System.out.println("Client with ID" + id + " disconnected. Closing connection...");
                        System.out.println("--------------------------------------");
                        pr.println("exit");
                        socket.close();
                        break;
                    }


                    int numberReceived = Integer.parseInt(clientMessage);
                    String scrapingResult = scraping(numberReceived);
                    System.out.println("Sending to Client with ID" + id + ":");
                    System.out.println(scrapingResult);
                    System.out.println("--------------------------------------");

                    pr.println(scrapingResult);
                    pr.println("EndOfSending");
                    pr.flush();
                }
                catch (IOException e)
                {
                    System.out.println("Client with ID " + id + " disconnected due to an error.");
                    System.out.println("--------------------------------------");
                    break;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("I/O error occurred in ClientHandler", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    public static String scraping(int numberOfLines)
    {
        StringBuilder result = new StringBuilder();
        int count = 0;
        Document doc;

        try
        {
            doc = Jsoup.connect("https://www.bankrate.com/investing/best-performing-stocks/#worst-snp-500").get();

            Elements tdElements = doc.select("td");

            for (int i = 0; i < tdElements.size() - 2;i+=2)
            {
                if(count == numberOfLines)
                {
                    break;
                }
                String key = tdElements.get(i).text();
                String value = tdElements.get(i+1).text();
                result.append(key).append(" - ").append(value).append("\n");
                count+=1;
            }

        }
        catch (IOException e)
        {
            result.append("Error occured during scraping: ").append(e.getMessage());
        }

        return result.toString();
    }


}
