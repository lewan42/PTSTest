package com.test;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;



public class Archive implements MyEntity {

    private String filePath;

    public Archive(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void start() {
        try (ZipFile zipFile = new ZipFile(filePath)) {
            Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
            Predicate<ZipEntry> isCSV = ze -> ze.getName().matches(".*csv");

            zipFile.stream()
                    .filter(isFile.and(isCSV))

                    .forEach(ze -> {

                        CSVReader csvReader = null;
                        try {
                            csvReader = new CSVReader(new InputStreamReader(zipFile.getInputStream(ze)));
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
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
