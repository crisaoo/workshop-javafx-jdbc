package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBtnNewAction() {
		System.out.println("Button Test");
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
