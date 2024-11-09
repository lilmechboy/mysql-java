package projects.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {

	public ProjectService() {
	}
	
	private static final String SCHEMA_FILE = "project_schema.sql";
	//private static final String DATA_FILE = "project_data.sql";
	
	private ProjectDao projectDao = new ProjectDao();
	
	public void createAndPopulateTables() {
		loadFromFile(SCHEMA_FILE);
		//loadFromFile(DATA_FILE);
	}

	
	private void loadFromFile(String fileName) {
		
		String content = readFileContent(fileName);
		List<String> sqlStatements = convertContentToSqlStatments(content);
		
		//sqlStatements.forEach(line -> System.out.println(line));
		
		projectDao.executeBatch(sqlStatements);
	}


	private List<String> convertContentToSqlStatments(String content) {
		content = removeComments(content);
		content = replaceWhitespaceSequenceWithSingleSpace(content);
		
		return extractLinesFromContent(content);
	}


	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();
		
		while(!content.isEmpty()) {
			int semicolon = content.indexOf(";");
			
			if (semicolon == -1) {
				
				if(!content.isBlank() ) {
					lines.add(content);
				}
				
				content = "";
			}
			
			else {
				lines.add(content.substring(0, semicolon).trim());
				content = content.substring(semicolon + 1);
				
			}
			
		}
			
		return lines;
	}


	private String replaceWhitespaceSequenceWithSingleSpace(String content) {
		return content.replaceAll("\\s+", " ");
	}


	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPosition = 0;
		
		while((commentPosition = builder.indexOf("-- ", commentPosition)) != -1) {
			int eolPosition = builder.indexOf("\n", commentPosition + 1);
			
			if(eolPosition == -1) {
				
				builder.replace(commentPosition, builder.length(), "");
			}
			else {
				
				builder.replace(commentPosition, eolPosition + 1, "");
			}
		}
		
		return builder.toString();
	}


	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}
	
	
//	public static void main(String[] args) {
//		new ProjectService().createAndPopulateTables();
//		
//	}


	public Project addProject(Project project) {
		
		return projectDao.insertProject(project);
	}
	
}
