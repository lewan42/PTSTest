package com.test;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Directory implements MyEntity {

    private String filePath;

    public Directory(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void start() {
        try {
            Files.newDirectoryStream(Paths.get(filePath),
                    path -> path.toString().endsWith(".csv"))
                    .forEach(e->{
                        System.err.println(e.toString());
                        CSVReader csvReader = null;

                        try {
                            csvReader = new CSVReader(new FileReader(String.valueOf(e)));
                            writeToList(csvReader);

                        } catch (FileNotFoundException ee) {
                            ee.printStackTrace();

                        } finally {
                            try {
                                assert csvReader != null;
                                csvReader.close();
                            } catch (IOException ee) {
                                ee.printStackTrace();
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
