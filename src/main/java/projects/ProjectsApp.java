package projects;

import projects.dao.DbConnection;
import projects.exception.DbException;
import projects.service.ProjectService;
import projects.entity.Project;


import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.math.BigDecimal;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	
	//@formatter:off
	private List<String> operations = List.of(
		"1) Create and populate all tables",
		"2) Add a project"
	);
	// @formatter:on

	public static void main(String[] args) {
		new ProjectsApp().displayMenu();

	}
	
	private void displayMenu() {
		boolean done = false;
		
		while(!done) {
			int operation = getOperation();
			
			try {
			switch(operation) {
			case -1:
				done = exitMenu();
				break;
				
			case 1:
				createTables();
				break;
				
			case 2:
				addProject();
				break;
				
			default:
				System.out.println("\n" + operation + " is not valid. Try again.");
				break;
			
			}
			} catch(Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again");
			}
		} 
		
	}
	
	private void addProject() {
		String name = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getBigDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getBigDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter how difficult the project is");
		String notes = getStringInput("Enter project notes");
		
		Project project = new Project();
		
		project.setProjectName(name);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You added this project: \n" + dbProject);
		
		
	}

	private void createTables() {
		projectService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");
		
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu.");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("Enter an operation number (Press Enter to quit)");
		
		return Objects.isNull(op) ? -1 : op;
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");
		
		operations.forEach(op -> System.out.println("	" + op));
				
	}
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.parseInt(input);
		}
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
	
	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Double.parseDouble(input);
		}
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
	
	private String getStringInput(String prompt) {
		System.out.println(prompt + ": ");
		String line = scanner.nextLine();
		
		return line.isBlank() ? null : line.trim();
	}
	
	private BigDecimal getBigDecimalInput(String prompt) {
		Double input = getDoubleInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return BigDecimal.valueOf(input);
		}
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid BigDecimal.");
		}
	}
	
}
