package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	private Department entity;
	private DepartmentService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label lblErrorName;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;

	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService (DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if (entity == null)
		// Caso eu esque�a de injetar depend�ncia l� no DepartmentListController
			throw new IllegalStateException("Entity was null");
		if (service == null)
			throw new IllegalStateException("Service was null");
		
		// O m�todo saveOrUpdate pode lan�ar uma exce��o, em vez disso, vamos lan�ar um alert
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();		// Atualizar os dados da tabela em tempo real
			Utils.currentStage(event).close();	// Fechar a janela de formul�rio
			
		}
		catch (ValidationException e) {
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
	}
	
	private Department getFormData() {
		ValidationException exception = new ValidationException("Validation error");
		
		Integer id = Utils.tryParseToInt(txtId.getText());
		String name = txtName.getText();
		
		if (name == null || name.trim().equals(""))
			exception.addError("name", "Field can't be empty");
		if (exception.getErrors().size() > 0)
			throw exception;
		
		return new Department(id, name);
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners)
			listener.onDataChanged();
	}
	
	private void setErrorMessages(Map <String, String> errors) {		
		// No caso do department, s� ir� ter erro no campo name
		if (errors.containsKey("name"))
			lblErrorName.setText(errors.get("name"));
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
}
