package com.test;

import javafx.scene.control.Alert;

public class AlertDialog {

    /**
     * Создаем окно для отображения информации пользователю
     *
     * @param message  сообщение которое будет выведено в окно
     * @param type     тип сообщения
     */
    public static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

