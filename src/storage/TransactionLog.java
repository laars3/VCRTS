package storage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

}