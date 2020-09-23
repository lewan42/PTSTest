package com.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {


    private static Map<String, Long> records = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);


    private static final String OUTPUT_FILE = "/Users/lewan/Desktop/json";
    private static final String INPUT_FILE = "/Users/lewan/Desktop/source_archive.zip";
    //private static final String INPUT_FILE = "/Users/lewan/Desktop/source_archive";


    public static void main(String[] args) {

        start();

        Map<String, Long> result = records.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        writeToFile(result);
    }

    public static void writeToFile(Map<String, Long> result) {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE));
            Gson gson = new GsonBuilder().create();
            gson.toJson(result, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void start() {
        try {

            Path filePath = Paths.get(INPUT_FILE);
            boolean fileExists = Files.exists(filePath);

            if (!fileExists) throw new FileNotFoundException("File not found");

            if (Files.isDirectory(filePath)) {

                readFiles(String.valueOf(filePath));

            } else if (String.valueOf(filePath).matches(".*zip")) {

                openZip(String.valueOf(filePath));

            } else throw new Exception("File format don`t supported");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readFiles(String filePath) {

        try {
            Files.newDirectoryStream(Paths.get(filePath),
                    path -> path.toString().endsWith(".csv"))
                    .forEach(Main::createReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createReader(Path fileName) {

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(String.valueOf(fileName)));
            read(csvReader);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } finally {
            try {
                assert csvReader != null;
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void read(CSVReader csvReader) {

        String[] values;
        try {

            while ((values = csvReader.readNext()) != null) {

                List<String> list_marks = Arrays.asList(values);
                if (String.valueOf(list_marks.get(0).charAt(0)).equals("#")) continue;

                String key = list_marks.get(0);
                long val = Long.parseLong(list_marks.get(1));

                if (records.containsKey(key)) {
                    val += records.getOrDefault(key, 0L);
                }

                records.put(key, val);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void openZip(String zip) {


        try (ZipFile zipFile = new ZipFile(zip)) {
            Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
            Predicate<ZipEntry> isCSV = ze -> ze.getName().matches(".*csv");

            zipFile.stream()
                    .filter(isFile.and(isCSV))
                    .forEach(ze -> createReader(ze, zipFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createReader(ZipEntry zipEntry, ZipFile zipFile) {

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
            read(csvReader);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                assert csvReader != null;
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
