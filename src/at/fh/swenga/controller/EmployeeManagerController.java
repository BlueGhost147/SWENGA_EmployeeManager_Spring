package at.fh.swenga.controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.model.EmployeeModel;
import at.fh.swenga.model.EmployeeService;

@Controller
public class EmployeeManagerController {

	@Autowired
	private EmployeeService employeeService;

	@RequestMapping(value = { "/", "listEmployees" })
	public String showAllEmployees(Model model) {
		model.addAttribute("employees", employeeService.getAllEmployees());
		return "listEmployees";
	}

	@RequestMapping("/fillEmployeeList")
	public String fillEmployeeList(Model model) {

		Date now = new Date();
		employeeService.addEmployee(new EmployeeModel(1, "Max", "Mustermann", now));
		employeeService.addEmployee(new EmployeeModel(2, "Mario", "Rossi", now));
		employeeService.addEmployee(new EmployeeModel(3, "John", "Doe", now));
		employeeService.addEmployee(new EmployeeModel(4, "Jane", "Doe", now));
		employeeService.addEmployee(new EmployeeModel(5, "Maria", "Noname", now));
		employeeService.addEmployee(new EmployeeModel(6, "Josef", "Noname", now));

		model.addAttribute("employees", employeeService.getAllEmployees());
		return "listEmployees";
	}

	// Spring 4: @RequestMapping(value = "/addEmployee", method = RequestMethod.GET)
	@GetMapping("/addEmployee")
	public String showAddEmployeeForm(Model model) {
		return "editEmployee";
	}

	// Spring 4: @RequestMapping(value = "/addEmployee", method =
	// RequestMethod.POST)
	@PostMapping("/addEmployee")
	public String addEmployee(@Valid EmployeeModel newEmployeeModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid: " + fieldError.getCode() + "<br>";
			}
			model.addAttribute("errorMessage", errorMessage);

			// Multiple ways to "forward"
			return "forward:/listEmployees";
		}

		// Look for employee in the List. One available -> Error
		EmployeeModel employee = employeeService.getEmployeeBySSN(newEmployeeModel.getSsn());

		if (employee != null) {
			model.addAttribute("errorMessage", "Employee already exists!<br>");
		} else {
			employeeService.addEmployee(newEmployeeModel);
			model.addAttribute("message", "New employee " + newEmployeeModel.getSsn() + " added.");
		}

		return "forward:/listEmployees";
	}

	// Spring 4: @RequestMapping(value = "/deleteEmployee", method =
	// RequestMethod.GET)
	@GetMapping("/deleteEmployee")
	public String delete(Model model, @RequestParam int ssn) {
		boolean isRemoved = employeeService.remove(ssn);

		if (isRemoved) {
			model.addAttribute("warningMessage", "Employee " + ssn + " deleted");
		} else {
			model.addAttribute("errorMessage", "There is no Employee " + ssn);
		}

		// Multiple ways to "forward"
		// return "forward:/listEmployees";
		return showAllEmployees(model);
	}

	// Spring 4: @RequestMapping(value = "/searchEmployees", method =
	// RequestMethod.POST)
	@PostMapping("/searchEmployees")
	public String search(Model model, @RequestParam String searchString) {
		model.addAttribute("employees", employeeService.getFilteredEmployees(searchString));
		return "listEmployees";
	}

	// Spring 4: @RequestMapping(value = "/editEmployee", method =
	// RequestMethod.GET)
	@GetMapping("/editEmployee")
	public String showChangeEmployeeForm(Model model, @RequestParam int ssn) {

		EmployeeModel employee = employeeService.getEmployeeBySSN(ssn);

		if (employee != null) {
			model.addAttribute("employee", employee);
			return "editEmployee";
		} else {
			model.addAttribute("errorMessage", "Couldn't find employee " + ssn);
			return "forward:/listEmployees";
		}
	}

	// Spring 4: @RequestMapping(value = "/editEmployee", method =
	// RequestMethod.POST)
	@PostMapping("/editEmployee")
	public String editEmployee(@Valid EmployeeModel changedEmployeeModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid: " + fieldError.getCode() + "<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return "forward:/listEmployees";
		}

		// Get the employee we want to change
		EmployeeModel employee = employeeService.getEmployeeBySSN(changedEmployeeModel.getSsn());

		if (employee == null) {
			model.addAttribute("errorMessage", "Employee does not exist!<br>");
		} else {
			// Change the attributes
			employee.setSsn(changedEmployeeModel.getSsn());
			employee.setFirstName(changedEmployeeModel.getFirstName());
			employee.setLastName(changedEmployeeModel.getLastName());
			employee.setDayOfBirth(changedEmployeeModel.getDayOfBirth());

			// Save a message for the web page
			model.addAttribute("message", "Changed employee " + changedEmployeeModel.getSsn());
		}

		return "forward:/listEmployees";
	}

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}
}
