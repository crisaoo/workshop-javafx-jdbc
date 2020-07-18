package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DBException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	private Department entity;
	private DepartmentService service;
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
	
	
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		// Caso eu esqueça de injetar dependência lá no DepartmentListController
		if (entity == null)
			throw new IllegalStateException("Entity was null");
		if (service == null)
			throw new IllegalStateException("Service was null");
		
		// O método saveOrUpdate pode lançar uma exceção, em vez disso, vamos lançar um alert
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			Utils.currentStage(event).close();
			
		}
		catch (DBException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService (DepartmentService service) {
		this.service = service;
	}
	
	public void updateFormData() {
		if (entity == null)
			throw new IllegalStateException("Entity was null");
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private Department getFormData() {
		Integer id = Utils.tryParseToInt(txtId.getText());
		String name = txtName.getText();
		
		return new Department(id, name);
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
