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

    private static List<List<String>> list_marks = new ArrayList<>();

    private static Map<String, Long> recordsForJsonOne = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static Map<String, Optional<Long>> recordsForJsonTwo = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static Map<String, List<Integer>> recordsForJsonThree = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static final String OUTPUT_FILE_1 = "/Users/lewan/Desktop/json1";
    public static final String OUTPUT_FILE_2 = "/Users/lewan/Desktop/json2";
    public static final String OUTPUT_FILE_3 = "/Users/lewan/Desktop/json3";
    private static final String INPUT_FILE = "/Users/lewan/Desktop/source_archive";


    public static void main(String[] args) throws Exception {

        addStartElement();

        start(INPUT_FILE);


        setFirstMap();
        setSecondMap();
        setThreeMap();


        writeToFile(OUTPUT_FILE_1, recordsForJsonOne);
        writeToFile_2(OUTPUT_FILE_2, recordsForJsonTwo);
        writeToFile_3(OUTPUT_FILE_3, recordsForJsonThree);
    }

    private static void setFirstMap() {
        list_marks.forEach(e -> {

            String key = e.get(0);

            long val = Long.parseLong(e.get(1));

            if (recordsForJsonOne.containsKey(key)) {
                val += recordsForJsonOne.getOrDefault(key, 0L);
            }

            recordsForJsonOne.put(key, val);
        });
    }

    private static void setThreeMap() {
        list_marks.forEach(e -> {

            String key = e.get(0);
            int val = Integer.parseInt(e.get(1));

            List<Integer> longList = new ArrayList<>();
            if (recordsForJsonThree.containsKey(key)) {
                longList = recordsForJsonThree.getOrDefault(key, new ArrayList<>());
            }

            longList.add(val);

            recordsForJsonThree.put(key, longList);
        });
    }

    private static void setSecondMap() {
        list_marks.forEach(e -> {

            String key = e.get(0);
            long val = Long.parseLong(e.get(1));

            if (recordsForJsonTwo.containsKey(key)) {
                if (recordsForJsonTwo.getOrDefault(key, Optional.empty()).isPresent())
                    val += recordsForJsonTwo.getOrDefault(key, Optional.empty()).get();
            }

            recordsForJsonTwo.put(key, Optional.of(val));
        });
    }


    private static void addStartElement() {

        recordsForJsonTwo.put("mark01", Optional.of(0L));
        recordsForJsonTwo.put("mark17", Optional.of(0L));
        recordsForJsonTwo.put("mark23", Optional.empty());
        recordsForJsonTwo.put("mark35", Optional.of(0L));
        recordsForJsonTwo.put("markFV", Optional.of(0L));
        recordsForJsonTwo.put("markFX", Optional.empty());
        recordsForJsonTwo.put("markFT", Optional.of(0L));
    }

    public static void writeToFile_3(String fileName, Map<String, List<Integer>> result) {

        Map<String, List<Integer>> map = new LinkedHashMap<>();

        result.forEach((k, v) -> {
            List<Integer> sortedList = v.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            map.put(k, sortedList);
        });

        writeToFile(fileName, map);
    }

    public static void writeToFile_2(String fileName, Map<String, Optional<Long>> result) {

        Map<String, Object> map = new HashMap<>();
        result.forEach((k, v) -> {
            if (v.isPresent())
                map.put(k, v.get());
            else map.put(k, null);
        });

        writeToFile(fileName, map);
    }

    public static void writeToFile(String fileName, Map<String, ?> result) {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(fileName));
            Gson gson = new GsonBuilder().serializeNulls().create();
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


    public static void start(String path) throws Exception {

        Path filePath = Paths.get(path);
        boolean fileExists = Files.exists(filePath);

        if (!fileExists) throw new FileNotFoundException();

        if (Files.isDirectory(filePath)) {

            readFiles(String.valueOf(filePath));

        } else if (String.valueOf(filePath).matches(".*zip")) {

            openZip(String.valueOf(filePath));

        } else throw new Exception("File format don`t supported");
    }


    public static void readFiles(String filePath) {

        try {
            Files.newDirectoryStream(Paths.get(filePath),
                    path -> path.toString().endsWith(".csv"))
                    .forEach(Main::createReader);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void createReader(Path fileName) {

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(String.valueOf(fileName)));
            writeToList(csvReader);

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

    private static void writeToList(CSVReader csvReader) {

        String[] values;
        try {

            while ((values = csvReader.readNext()) != null) {

                if (String.valueOf(Arrays.asList(values).get(0).charAt(0)).equals("#")) continue;
                list_marks.add(Arrays.asList(values));
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

    public static void createReader(ZipEntry zipEntry, ZipFile zipFile) {

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
            writeToList(csvReader);
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

    public static Map<String, Long> getRecordsForJsonOne() {
        return recordsForJsonOne;
    }

    public static Map<String, Optional<Long>> getRecordsForJsonTwo() {
        return recordsForJsonTwo;
    }

    public static Map<String, List<Integer>> getRecordsForJsonThree() {
        return recordsForJsonThree;
    }
}
