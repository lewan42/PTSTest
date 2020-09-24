package com.test;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Main extends Application {

    final FileChooser fileChooser = new FileChooser();
    final DirectoryChooser directoryChooser = new DirectoryChooser();

    /**
     * Создает интерфейс
     *
     * @param primaryStage представляет пользовательский интерфейс
     */

    @Override
    public void start(Stage primaryStage) {

        Label label_dir_or_arch = new Label("Выберите директорию или архив");

        TextField tf_dir_or_arch = new TextField();

        Button btn_choose_folder = new Button("Выбрать папку");

        btn_choose_folder.setOnAction(event -> {
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                tf_dir_or_arch.setText(dir.getAbsolutePath());
            } else {
                tf_dir_or_arch.setText(null);
            }
        });

        Button btn_choose_archive = new Button("Выбрать zip архив");

        btn_choose_archive.setOnAction(event -> {
            tf_dir_or_arch.clear();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                printToTextField(tf_dir_or_arch, file);
            }
        });

        HBox root = new HBox();

        setPaddingNode(root, 10);

        HBox container = new HBox();
        container.setSpacing(50);

        VBox vb_locale_in = new VBox();
        vb_locale_in.setSpacing(10);
        vb_locale_in.getChildren().addAll(label_dir_or_arch, btn_choose_folder, btn_choose_archive, tf_dir_or_arch);
        container.getChildren().add(vb_locale_in);

        Label label_save_locale_folder = new Label("Выберите папку для сохранения");
        Button btn_save_locale_folder = new Button("Выберите папку для сохранения");
        TextField ft_save_locale_folder = new TextField();

        btn_save_locale_folder.setOnAction(event -> {
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                ft_save_locale_folder.setText(dir.getAbsolutePath());
            } else {
                ft_save_locale_folder.setText(null);
            }
        });


        Label fileName1 = new Label("Введите имя файла 1");
        Label fileName2 = new Label("Введите имя файла 2");
        Label fileName3 = new Label("Введите имя файла 3");


        TextField tf1 = new TextField();
        TextField tf2 = new TextField();
        TextField tf3 = new TextField();

//        tf_dir_or_arch.setText(String.valueOf("/Users/lewan/Desktop/source_archive/"));
//        ft_save_locale_folder.setText(String.valueOf("/Users/lewan/Desktop"));
//
//
//        tf1.setText("j1");
//        tf2.setText("j2");
//        tf3.setText("j3");


        Button btn_start = new Button("Старт");

        btn_start.setOnMouseClicked(e -> {
            if (tf_dir_or_arch.getText() == null || tf_dir_or_arch.getText().isEmpty()) {
                AlertDialog.showAlert("Укажите путь до папки или архива", Alert.AlertType.WARNING);
                return;

            } else if (ft_save_locale_folder.getText() == null || ft_save_locale_folder.getText().isEmpty()) {
                AlertDialog.showAlert("Укажите место сохранения", Alert.AlertType.WARNING);
                return;

            } else if (tf1.getText() == null || tf1.getText().isEmpty()) {
                AlertDialog.showAlert("Укажите имя папки 1", Alert.AlertType.WARNING);
                return;

            } else if (tf2.getText() == null || tf2.getText().isEmpty()) {
                AlertDialog.showAlert("Укажите имя папки 2", Alert.AlertType.WARNING);
                return;

            } else if (tf3.getText() == null || tf3.getText().isEmpty()) {
                AlertDialog.showAlert("Укажите имя папки 3", Alert.AlertType.WARNING);
                return;

            }

            MyEntity file = null;
            try {
                file = getFileObject(tf_dir_or_arch.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            assert file != null;
            file.start();

            if (file.getList_marks().isEmpty()) {
                AlertDialog.showAlert("Не удалось обнаружить исходные данные", Alert.AlertType.WARNING);
                return;
            }


            Map<String, Long> map_1 = setFirstMap(file.getList_marks(), new TreeMap<>(String.CASE_INSENSITIVE_ORDER));

            Map<String, Optional<Long>> map_2 = setSecondMap(file.getList_marks(), new TreeMap<>(String.CASE_INSENSITIVE_ORDER));

            Map<String, List<Integer>> map_3 = setThreeMap(file.getList_marks(), new TreeMap<>(String.CASE_INSENSITIVE_ORDER));


            writeMapToJSONFile(ft_save_locale_folder.getText() + "/" + tf1.getText() + ".json", map_1);
            writeMapToJSONFile(ft_save_locale_folder.getText() + "/" + tf2.getText() + ".json", changeEmptyToNull(map_2));
            writeMapToJSONFile(ft_save_locale_folder.getText() + "/" + tf3.getText() + ".json", sortValuesMap(map_3));

            AlertDialog.showAlert("Слияние данных завершено успешно", Alert.AlertType.INFORMATION);
        });


        VBox vb_container = new VBox();
        vb_container.getChildren().addAll(label_save_locale_folder, btn_save_locale_folder, ft_save_locale_folder, fileName1, tf1, fileName2, tf2, fileName3, tf3, btn_start);
        vb_container.setSpacing(10);
        container.getChildren().add(vb_container);


        root.getChildren().addAll(container);

        Scene scene = new Scene(root, 515, 350);

        primaryStage.setTitle("TEST");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Устанавливает отступ по всем сторонам
     *
     * @param node графический элемент
     * @param val  целочисленной значение отступа
     */

    public static void setPaddingNode(Node node, int val) {
        ((Pane) node).setPadding(new Insets(val));
    }

    /**
     * Устанавливает отступ по всем сторонам
     *
     * @param tf   TextField, в который будет записано значение пути файла
     * @param file Файл
     */

    public static void printToTextField(TextField tf, File file) {
        if (file == null) {
            return;
        }

        tf.appendText(file.getAbsolutePath() + "\n");
    }


    public static void main(String[] args) {
        Application.launch(args);
    }


    /**
     * Добавляем маркеры в map для первого JSON файла, при этом складываем значение одинаковых маркеров
     *
     * @param list_marks список маркеров
     * @param map        в нее записываем маркеры
     * @return возвращаем map, которую будем записывать в файл
     */
    public static Map<String, Long> setFirstMap(List<List<String>> list_marks, Map<String, Long> map) {
        list_marks.forEach(e -> {

            String key = e.get(0);
            long val = Long.parseLong(e.get(1));

            if (map.containsKey(key)) {
                val += map.getOrDefault(key, 0L);
            }

            map.put(key, val);
        });

        return map;
    }

    /**
     * Добавляем маркеры в map для второго JSON файла, с уже дефолтными данными.
     *
     * @param list_marks список маркеров
     * @param map        в нее записываем маркеры
     * @return возвращаем map, которую будем записывать в файл
     */
    private static Map<String, Optional<Long>> setSecondMap(List<List<String>> list_marks, Map<String, Optional<Long>> map) {

        setDefaultValue(map);
        list_marks.forEach(e -> {

            String key = e.get(0);
            long val = Long.parseLong(e.get(1));

            if (map.containsKey(key)) {
                if (map.getOrDefault(key, Optional.empty()).isPresent())
                    val += map.getOrDefault(key, Optional.empty()).get();
            }

            map.put(key, Optional.of(val));
        });
        return map;
    }


    /**
     * Создаем список значений одинаковых маркеров
     * Добавляем маркеры в map для третьего JSON файла
     *
     * @param list_marks список маркеров
     * @param map        в нее записываем маркеры
     * @return возвращаем map, которую будем записывать в файл
     */
    private static Map<String, List<Integer>> setThreeMap(List<List<String>> list_marks, Map<String, List<Integer>> map) {
        list_marks.forEach(e -> {

            String key = e.get(0);
            int val = Integer.parseInt(e.get(1));

            List<Integer> longList = new ArrayList<>();
            if (map.containsKey(key)) {
                longList = map.getOrDefault(key, new ArrayList<>());
            }

            longList.add(val);
            map.put(key, longList);
        });

        return map;
    }


    /**
     * Записываем дефолтные значения в карту для второго JSON файла
     *
     * @param map в нее записываем маркеры
     */
    private static void setDefaultValue(Map<String, Optional<Long>> map) {

        map.put("mark01", Optional.of(0L));
        map.put("mark17", Optional.of(0L));
        map.put("mark23", Optional.empty());
        map.put("mark35", Optional.of(0L));
        map.put("markFV", Optional.of(0L));
        map.put("markFX", Optional.empty());
        map.put("markFT", Optional.of(0L));
    }

    /**
     * Сортируем список значений.
     *
     * @param result состоящая из маркера и списка его значений
     * @return возвращаем map у которой список ее значений отсортированан по убыванию
     */
    public static Map<String, List<Integer>> sortValuesMap(Map<String, List<Integer>> result) {

        Map<String, List<Integer>> map = new LinkedHashMap<>();

        result.forEach((k, v) -> {
            List<Integer> sortedList = v.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            map.put(k, sortedList);
        });

        return map;
    }

    /**
     * Для ключей чьё значение пусто, меняем его на null.
     *
     * @param result ассоциативный список, у которого некоторые значения равны пустоте
     * @return ассоциативный список, у которого некоторые значения равны null
     */
    public static Map<String, Object> changeEmptyToNull(Map<String, Optional<Long>> result) {

        Map<String, Object> map = new HashMap<>();

        result.forEach((k, v) -> {
            if (v.isPresent())
                map.put(k, v.get());
            else map.put(k, null);
        });

        return map;
    }

    /**
     * Записываем данные в JSON файл
     *
     * @param pathName путь и название файла в который будут записаны данные
     * @param result   данные
     */
    public static void writeMapToJSONFile(String pathName, Map<String, ?> result) {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(pathName));
            Gson gson = new GsonBuilder().serializeNulls().create();
            gson.toJson(result, writer);
        } catch (IOException e) {
            AlertDialog.showAlert("Папка для сохранения не найдена\nПроверте имя пути", Alert.AlertType.ERROR);
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

    /**
     * Для ключей чьё значение пусто, меняем его на null.
     *
     * @param path путь до директории или архива
     * @return возвращаем объект типа директории или архива
     */
    public static MyEntity getFileObject(String path) throws Exception {

        Path filePath = Paths.get(path);
        boolean fileExists = Files.exists(filePath);

        if (!fileExists) {
            AlertDialog.showAlert("Исходные данные не найдены\nПроверте имя пути", Alert.AlertType.ERROR);
            throw new FileNotFoundException("File not found");
        }

        if (Files.isDirectory(filePath)) {
            return new Directory(String.valueOf(filePath));

        } else if (String.valueOf(filePath).matches(".*zip")) {
            return new Archive(String.valueOf(filePath));

        } else {
            AlertDialog.showAlert("Данный формат данных не поддерживается", Alert.AlertType.ERROR);
            throw new Exception("File format don`t supported");
        }
    }
}
