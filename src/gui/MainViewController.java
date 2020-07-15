package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable{
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentListView.fxml", (DepartmentListController controller) ->{
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableViewData();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	// synchronized: para que não ocorra erro em tempo de execução
	// T: generics
	// Consumer: uma ação
	private synchronized <T> void loadView (String path, Consumer <T> initializingAction) {
		try {
			// Carrego a tela de acordo com o caminho do arquivo .fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			VBox newVBox = loader.load();
			
			// Pego a VBox da cena principal e guardo-a
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
			
			// Crio um node para guardar a barra de menu
			Node mainMenu = mainVBox.getChildren().get(0);
			// Adiciono ao main vbox a barra de menu e o conteúdo do arquivo contido no path
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//Para que a ação, no qual foi parametrizada, ocorra
			T controller = loader.getController();
			initializingAction.accept(controller);
		}
		catch (IOException e) {
			Alerts.showAlert("Error", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
