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

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(5000);

        try {

            while (true) {
                System.out.println("Server is running...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);

                while (true) {

                    String clientMessage = bf.readLine();
                    if (clientMessage == null || clientMessage.equals("exit")) {
                            System.out.println("Client disconnected. Closing connection...");
                            pr.println("exit");
                        // dobaveno sled trugvane
                        socket.close();
                        break;
                    } else if (clientMessage.equalsIgnoreCase("admin")) {
                        pr.println("admin");
                        pr.close();
                        bf.close();
                        in.close();
                        socket.close();
                        serverSocket.close();
                        return;
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
        catch (IOException | NumberFormatException e)
        {
            System.out.println("Error: " + e.getMessage());
        }


    }
}



/*
public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(5000);

    System.out.println("Server is running...");

    while (true) {
        // Приемане на нов клиент
        Socket clientSocket = serverSocket.accept();
        System.out.println("New client connected!");

        // Стартиране на нова нишка за клиента
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        Thread thread = new Thread(clientHandler);
        thread.start();
    }
}

 */









/* class ClientHandler implements Runnable {
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
*/
