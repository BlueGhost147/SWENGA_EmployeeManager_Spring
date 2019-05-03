package at.fh.swenga.model;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
 
@Repository
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS, value = "session")
public class EmployeeService {
 
	private List<EmployeeModel> employees = new ArrayList<EmployeeModel>();
 
	/**
	 * Add employee to List
	 * @param employee
	 */
	public void addEmployee(EmployeeModel employee) {
		employees.add(employee);
	}
 
	/**
	 * Verify if list contains employee with same SSN
	 * @param employee
	 * @return
	 */
	public boolean contains(EmployeeModel employee) {
		return employees.contains(employee);
	}
	/**
	 * convenient method: true if list is empty
	 * @return
	 */
	public boolean isEmpty() {
		return employees.isEmpty();
	}
 
	/**
	 * try to find EmployeeModel with given SSN
	 * return model, otherwise null
	 * @param ssn
	 * @return
	 */
	public EmployeeModel getEmployeeBySSN(int ssn) {
 
		for (EmployeeModel employeeModel : employees) {
			if (employeeModel.getSsn() == ssn) {
				return employeeModel;
			}
		}
		return null;
	}
 
	/**
	 * return list with all employees
	 * @return
	 */
	public List<EmployeeModel> getAllEmployees() {
		return employees;
	}
 
	/**
	 * return a new list with all employees where firstname or lastname contains search string
	 * @param searchString
	 * @return
	 */
	public List<EmployeeModel> getFilteredEmployees(String searchString) {
 
		if (searchString == null || searchString.equals("")) {
			return employees;
		}
 
		// List for results
		List<EmployeeModel> filteredList = new ArrayList<EmployeeModel>();
 
		// check every employee
		for (EmployeeModel employeeModel : employees) {
 
			if ((employeeModel.getFirstName() != null && employeeModel.getFirstName().contains(searchString))
					|| (employeeModel.getLastName() != null && employeeModel.getLastName().contains(searchString))) {
				filteredList.add(employeeModel);
			}
		}
		return filteredList;
	}
 
	/**
	 * remove employees with same ssn
	 * @param ssn
	 * @return
	 */
	public boolean remove(int ssn) {
		return employees.remove(new EmployeeModel(ssn, null, null, null));
	}
}