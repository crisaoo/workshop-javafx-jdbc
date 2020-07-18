package model.services;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentService {
	private DepartmentDAO  dao = DAOFactory.createDepartmentDAO();
	
	public List<Department> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Department obj) {
		if (obj.getId() == null)
			dao.insert(obj);
		else
			dao.update(obj);
	}
}
