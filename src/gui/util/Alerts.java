package gui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class Alerts {
	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.show(); 
	}
	
	public static Optional<ButtonType> showConfirmation(String title, String content){
		// Criar um botão "Cancel" em vez de "Cancelar"
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		
		Alert alert = new Alert(AlertType.CONFIRMATION, content, ButtonType.OK, cancelButton);
		alert.setTitle(title);
		alert.setHeaderText(null);
		return alert.showAndWait();
	}
}
