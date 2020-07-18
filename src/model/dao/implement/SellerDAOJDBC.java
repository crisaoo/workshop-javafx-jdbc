package model.dao.implement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class SellerDAOJDBC implements SellerDAO {
	private Connection conn;
	
	public SellerDAOJDBC(Connection conn) {
		this.conn = conn;
	}
	
	
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO seller (name, email, birthdate, basesalary, departmentid)"
								+ "VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next())
					obj.setId(rs.getInt(1));
				DB.closeResultSet(rs);
			}
			else
				throw new DBException("Unexpected error: no rows affected.");
		}
		catch(SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);				
		}		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("UPDATE seller SET name = ?, email =?, birthdate ?, basesalary = ?, departmentid =?"
								+ "WHERE id =?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0)
				throw new DBException("Error: ID not found.");
			if (rowsAffected > 1)
				throw new DBException("Error: Safe update is enabled.");				
			
			conn.commit();
		}
		catch(SQLException e) {
			try {
				conn.rollback();
			}
			catch(SQLException f) {
				f.printStackTrace();
			}
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);				
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement("DELETE FROM seller WHERE id = ?");
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0)
				throw new DBException("Error: ID not found.");
			if (rowsAffected > 1)
				throw new DBException("Error: Safe update is enabled.");	
			
			conn.commit();
		}
		catch(SQLException e) {
			try {
				conn.rollback();
			}
			catch(SQLException f) {
				f.printStackTrace();
			}
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);				
		}	
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*, department.name as departmentName FROM seller INNER JOIN department"
										+ "ON seller.departmentId = department.id where seller.id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next())
				return instantiateSeller(rs);
			
			return null;
		}
		catch(SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);			}	
		}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*, department.name AS departmentName FROM seller INNER JOIN department"
										+ "ON seller.departmentId = department.id WHERE departmentId = ?");
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			while(rs.next())
				sellers.add(instantiateSeller(rs, department));
			
			return sellers;
		}
		catch(SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT seller.*, department.name AS departmentName  FROM Seller INNER JOIN Department"
										+ "ON seller.departmentId = department.id");
			rs = st.executeQuery();
			
			List <Seller> allSellers = new ArrayList<>();
			Map<Integer, Department> departments = new HashMap<>();
			
			while(rs.next()) {
				Integer key = rs.getInt("departmentId");
				if(!departments.containsKey(key))
					departments.put(key, instantiateDepartment(rs));
				
				allSellers.add(instantiateSeller(rs, departments.get(key)));
			}
			
			return allSellers;
		}
		catch(SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);		}
	}

	
	private Seller instantiateSeller(ResultSet rs) throws SQLException {
		Integer id = rs.getInt("id");
		String name = rs.getString("name");
		String email = rs.getString("email");
		Date birthDate = rs.getDate("birthDate");
		Double baseSalary = rs.getDouble("baseSalary");
		Department department = instantiateDepartment(rs);
		return new Seller(id, name, email, birthDate, baseSalary, department);
	}
	
	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Integer id = rs.getInt("id");
		String name = rs.getString("name");
		String email = rs.getString("email");
		Date birthDate = rs.getDate("birthDate");
		Double baseSalary = rs.getDouble("baseSalary");
		return new Seller(id, name, email, birthDate, baseSalary, department);
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException{
		Integer id = rs.getInt("departmentId");
		String name = rs.getString("departmentName");
		return new Department(id, name);
	}
}
