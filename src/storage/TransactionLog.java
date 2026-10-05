package storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class TransactionLog{

    private static final String FILE_NAME = "tLog.txt";

    public static void append(String entry){
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try(PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME, true))){
            out.println("Time: " + time + ", " + entry);
        } catch (IOException e){
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Reads every logged line, empty if the file isnt there yet.
    public static ArrayList<String> readAll(){
        ArrayList<String> lines = new ArrayList<String>();
        try(BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while ((line = in.readLine()) != null){
                lines.add(line);
            }
        } catch (IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }

}