package model.services;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.SellerDAO;
import model.entities.Seller;

public class SellerService {
	private SellerDAO dao = DAOFactory.createSellerDAO();
	
	public List<Seller> findAll (){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null)
			dao.insert(obj);
		else
			dao.update(obj);
	}
	
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
}
