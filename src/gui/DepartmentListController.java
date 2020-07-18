package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	private DepartmentService service;
	private ObservableList<Department> obsList;
	@FXML
	private Button btnNew;
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColId;
	@FXML
	private TableColumn<Department, String> tableColName;
	
	
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		// Método para retornar o palco principal, no qual o formulário irá "sobrepor"
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm("/gui/DepartmentForm.fxml", parentStage, obj);
	}
	
	public void setDepartmentService(DepartmentService service) {
		// Delegando a função de instanciar o department service à outra função, para n gerar uma dependência tão forte
		this.service = service;
	}
	
	public void updateTableViewData() {
		if (service == null)
			throw new IllegalStateException("Service was null.");
		
		// Adicionar a lista de departamentos à tableView
		List <Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}

	private void createDialogForm(String absoluteName, Stage parentStage, Department obj) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			// Injetando dependência e atualizando o formulário
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);					// A janela n irá ser redimensionável
			dialogStage.initOwner(parentStage);					// Qual janela o formulário irá sobrepor
			dialogStage.initModality(Modality.WINDOW_MODAL);	// Para ser uma janela modal (não posso clicar ou fechar a janela anterior)
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		// Inicializar os nodos da tableView
		tableColId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// Para que a tabela se adapte ao redimensionamento da tela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
}
