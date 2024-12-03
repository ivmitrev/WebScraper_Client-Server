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
import java.util.Enumeration;
import java.util.Scanner;

public class WebScraperServer
{

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

    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(5000);

        while(true)
        {
            System.out.println("Server is running...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);

            while (true)
            {
                String clientMessage = bf.readLine();
                if (clientMessage == null)
                {
                    System.out.println("Client disconnected. Closing connection...");
                    break;
                }
                if (clientMessage.equalsIgnoreCase("admin"))
                {
                    pr.println("Server shutting down...");
                    pr.close();
                    bf.close();
                    in.close();
                    socket.close();
                    serverSocket.close();
                    return;
                }
                else if (clientMessage.equalsIgnoreCase("Client disconnected!"))
                {
                    // ne mi trqbvaaa!
                    System.out.println("Client disconnected!");
                    break;
                }

                int numberReceived = Integer.parseInt(clientMessage);
                String scrapingResult = scraping(numberReceived);
                System.out.println(scrapingResult);
                //System.out.println("client: " + clientMessage);

                pr.println(scrapingResult);
                pr.println("EndOfSending");
                pr.flush();
            }
        }

        //System.out.println("Admin command received. Shutting down server...");
    }
}
