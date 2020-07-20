package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{
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
	private TableColumn<Department, Department> tableColEdit;
	@FXML
	private TableColumn<Department, Department> tableColRemove;
	
	
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
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(String absoluteName, Stage parentStage, Department obj) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			// Injetando dependência e atualizando o formulário
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
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
	
	private void initEditButtons() {
		tableColEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColEdit.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("edit");
			
			
			@Override
			protected void updateItem (Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> createDialogForm("/gui/DepartmentForm.fxml", Utils.currentStage(event), obj));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColRemove.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem (Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void removeEntity(Department entity) {
		Optional<ButtonType> confirmation =  Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		if (confirmation.get() == ButtonType.OK) {
			if(service == null)
				throw new IllegalStateException("Service was null");
			try {
				service.remove(entity);
				updateTableViewData();
			}
			catch (DBException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	
	@Override
	public void onDataChanged() {
		updateTableViewData();
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
