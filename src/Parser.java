/*
  Parser.java program read files from txt_files folder and convert all of the
  to sms-call-internet-tn or sms-call-internet-tz CSV file. The program read line
  by line from txt file and split the data based on tab spaces and store them in
  the dataLine list object. Finally we save the data from dataLine list to CSV file
  under each respective header.

  Author: Kashif Ali
  Date: 09/09/2022
  property: Capgemini
  Project: TETHYS

 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    List<String[]> dataLines = new ArrayList<>();
    List<String> files_tn = new ArrayList<>();

    final static File folder = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "txt_files");
    final String CSV_FILE_NAME = System.getProperty("user.dir") + File.separator + "src" + File.separator + "sms-call-internet-tn.csv";


    // program execution starts here
    public static void main(String[] args) throws IOException {
        Parser fileParser = new Parser();

        fileParser.listFilesForFolder(folder);
        fileParser.parse_tn_files();

    }

    // file parsing is done here
    public void parse_tn_files() throws IOException {

        // Steps
        // 1. read data from file
        // 2. iterate through input data
        // 3. convert data to csv format
        // 4. append data to file
        // 5. Add headers to csv file
        // 6. Read all files from folder and save into String list


        // add header to csv files
        addHeaderToCSVFile_tn();

        // read all files data into dataLine list
        for(int file_count=0; file_count<files_tn.size(); file_count++) {
            readDataFromFile(folder, files_tn.get(0));
        }

        // save file to csv
        saveDataToCSVFile();
        System.out.println("Total Files Processed [" + files_tn.size() + "]");
        System.out.println("Total data processed [" +dataLines.size()/1000 + "]" + "Kb");
        System.out.println("Finished Successfully!");

    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
                files_tn.add(fileEntry.getName());
            }
        }
    }

    public void addHeaderToCSVFile_tn() {
        dataLines.add(new String[]{"Square Id", "Time Interval", "SMS-in activity", "SMS-out activity", "Call-in activity", "Call-out actvitiy",
                "Internet traffic activity", "Country Code"});
    }

    public void readDataFromFile(final File folder, String filename) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            System.out.println("filepath:" + folder.getPath() + File.separator + filename );
            inputStream = new FileInputStream(folder.getPath()+ File.separator + filename);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line.split("\t"));

                dataLines.add(line.split("\t"));
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public void saveDataToCSVFile() throws IOException {
        File csvOutputFile = new File(CSV_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        dataLines.clear();
    }


}
