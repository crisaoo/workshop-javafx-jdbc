package model.dao.implement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentDAOJDBC implements DepartmentDAO {
	private Connection conn;
	
	public DepartmentDAOJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("INSERT INTO department (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next())
					obj.setId(rs.getInt(1));
				DB.closeResultSet(rs);
			}
			else 
				throw new DBException("Unexpected error! No rows affected!");
						
		} 
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;

		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("UPDATE Department SET name = ? WHERE id = ? ");
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0)
				throw new SQLException("Error: Id not found.");
			if (rowsAffected > 1)
				throw new SQLException("Error: Safe update is enabled.");
				
			
			conn.commit();
		} 
		catch (SQLException e) {
			try {
				conn.rollback();
			}
			catch (SQLException f) {
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
			
			st = conn.prepareStatement("DELETE FROM Department WHERE id = ?");
			st.setInt(1, id);
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 0)
				throw new SQLException("Error: Id not found.");
			if (rowsAffected > 1)
				throw new SQLException("Error: Safe update is enabled.");
				
			
			conn.commit();
		} 
		catch (SQLException e) {
			try {
				conn.rollback();
			}
			catch (SQLException f) {
				f.printStackTrace();
			}
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT * FROM Department WHERE id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next())
				return instantiateDepartment(rs);
			
			return null;
		} 
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT * FROM department");
			rs = st.executeQuery();
			
			List<Department> allDepartments = new ArrayList<>();
			while(rs.next()) 
				allDepartments.add(instantiateDepartment(rs));
			
			return allDepartments;
		} 
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Integer id = rs.getInt("id");
		String name = rs.getString("name");
		
		return new Department(id, name);
	}

}
