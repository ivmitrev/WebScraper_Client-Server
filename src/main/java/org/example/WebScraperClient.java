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

            System.out.print("Please enter a number: ");
            String input = scanner.nextLine();
            if(input.equals("admin"))
            {
                return -2;
            }
            else if(input.equals("exit"))
            {
                return -1;
            }

            try
            {
                number = Integer.parseInt(input);
                if(number <= 0)
                {
                    throw new NumberFormatException("Input must be a positive number and not zero!");
                }
                flagForInput = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Invalid input. Please enter a valid integer!");
            }


        }
        System.out.println("You entered: " + number);
        return number;
    }


    public static void main(String[] args) throws IOException
    {
       Socket socket = new Socket("localhost",5000);
       PrintWriter pr = new PrintWriter(socket.getOutputStream());

       InputStreamReader in = new InputStreamReader(socket.getInputStream());
       BufferedReader bf = new BufferedReader(in);

       while(true)
       {
           int number = getInput();
           if(number == -2)
           {
              pr.println("admin");
              //break;
           }
           else if(number == -1)
           {
               // ne mi trqbvaa
               pr.println("exit");
               //break;
           }
           else
           {
               pr.println(number);
           }

           pr.flush();

           String line;
           System.out.println("Server: ");
           while(!((line = bf.readLine()).equals("EndOfSending")))
           {
               if(line.equals("admin"))
               {
                   socket.close();
                   bf.close();
                   pr.close();
                   in.close();
                   System.out.println("Server and client shutting down!");
                   return;
               }
               else if(line.equals("exit"))
               {
                   socket.close();
                   bf.close();
                   pr.close();
                   in.close();
                   System.out.println("Client exiting!");
                   return;
               }
               System.out.println(line);
           }

       }


    }

}

//TODO dolu da implementiram komentara v survura
//TODO da sloja try catch s ioexception e