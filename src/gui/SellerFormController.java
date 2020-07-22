package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	private Seller entity;
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	private ObservableList<Department> obsList;
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;
	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Label lblErrorBaseSalary;
	
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}
		
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if (entity == null)
			throw new IllegalStateException("Entity was null");	
		if (service== null)
			throw new IllegalStateException("Service was null");
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DBException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void updateFormData() {
		if (entity == null)
			throw new IllegalStateException("Entity was null");
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if (entity.getBirthDate() != null)
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // Conversão para datePicker + o formato padrão da máquina
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getDepartment() == null)
			comboBoxDepartment.getSelectionModel().selectFirst();
		else
			comboBoxDepartment.setValue(entity.getDepartment());
	}
	
	private Seller getFormData() {
		ValidationException exception = new ValidationException("Validation Exception");
		
		Integer id = Integer.parseInt(txtId.getText());
		String name = txtName.getText();
		String email = txtEmail.getText();
		Date birthDate = null;
		Double baseSalary = Double.parseDouble(txtBaseSalary.getText());
		
		if (name == null || name.trim().equals(""))
			exception.addError("name", "Field can't be empty");
		if (exception.getErrors().size() > 0)
			throw exception;

		return new Seller(id, name, email, birthDate, baseSalary, null);
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners)
			listener.onDataChanged();
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		if (errors.containsKey("name"))
			lblErrorName.setText(errors.get("name"));
	}

	public void loadAssociatedObjects() {
		if (departmentService == null)
			throw new IllegalStateException("DepartmentService was null");
		
		List<Department> list =  departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(txtBaseSalary);
		initializeComboBoxDepartment();
	}
	
	private void initializeComboBoxDepartment() {
		// Para que, nas opções da comboBox, apareça apenas o nome do departmento
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
	
}
