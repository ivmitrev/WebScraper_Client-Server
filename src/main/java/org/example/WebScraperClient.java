package org.example;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class WebScraperClient
{
    public static int getInput()
    {
        Scanner scanner = new Scanner(System.in);
        int number = 0;
        boolean flagForInput = false;
        while (!flagForInput)
        {

            System.out.print("Please enter a number or exit: ");
            String input = scanner.nextLine();
            if(input.equals("exit"))
            {
                return -1;
            }

            try
            {
                number = Integer.parseInt(input);
                if(number <= 0)
                {
                    throw new IllegalArgumentException("Input must be a positive number and not zero!");
                }
                flagForInput = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Invalid input. Please enter a valid integer or valid command!");
            }
            catch (IllegalArgumentException e)
            {
                System.out.println(e.getMessage());
            }


        }
        System.out.println("You entered: " + number);
        return number;
    }


    public static void main(String[] args) throws IOException
    {
       try( Socket socket = new Socket("localhost",5000);
            PrintWriter pr = new PrintWriter(socket.getOutputStream());
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bf = new BufferedReader(in) ) {

           while (true) {
               int number = getInput();
               if (number == -1) {
                   pr.println("exit");
               } else {
                   pr.println(number);
               }

               pr.flush();

               String line;
               System.out.println("Server: ");
               while (!((line = bf.readLine()).equals("EndOfSending"))) {
                   if (line.equals("exit")) {
                       socket.close();
                       bf.close();
                       pr.close();
                       in.close();
                       System.out.println("Client exiting!");
                       return;
                   }
                   System.out.println(line);
               }
               System.out.println("--------------------------------------");

           }
       }
       catch (IOException e)
       {
           System.err.println("Error: " + e.getMessage());
       }

    }

}
