package com.test;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface MyEntity {

    List<List<String>> list_marks = new ArrayList<>();

    void start();

    /**
     * Читаем данные и записываем иx в лист
     *
     * @param csvReader  объект для чтения csv файлов
     */
    default void writeToList(CSVReader csvReader) {

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

    default List<List<String>> getList_marks() {
        return list_marks;
    }
}
