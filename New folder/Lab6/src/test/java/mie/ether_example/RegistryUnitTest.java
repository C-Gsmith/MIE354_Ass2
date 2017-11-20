package mie.ether_example;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.toronto.dbservice.types.EtherAccount;

// Run with parameters
@RunWith(Parameterized.class)
public class RegistryUnitTest extends LabBaseUnitTest {
	
	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/MyProcess.bpmn";
	}
	
	// START OF PARAMETERIZED CODE
	private String aItemParameter;
	private String aMediumParameter;
	private String aOwnerParameter;
	
	// Constructor has two string parameters
	public RegistryUnitTest(String itemParam, String mediumParam, String ownerParam) {
		this.aItemParameter = itemParam;
		this.aMediumParameter=mediumParam;
		this.aOwnerParameter = ownerParam;
	}
	
	// Setup parameters to provide pairs of strings to the constructor
	@Parameters
	public static Collection<String[]> data() {
		ArrayList<String[]> parameters = new ArrayList<>();
		parameters.add(new String[] {"modern madness", "pipe cleaners","1"});
		parameters.add(new String[] {"life is a database", "digital", "2"});
		parameters.add(new String[] {"java: an odyssey","mixed media", "3"});
		parameters.add(new String[] {"total Eclipse of my heart","tears", "5"});
		parameters.add(new String[] {"Dreaming of the green","JUnit" ,"7"});
		return parameters;
	}
	// END OF PARAMETERIZED CODE
	
	private void startProcess() {	
		RuntimeService runtimeService = activitiContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("process_pool1");
	}
	
	/*private void fillAuditForm(String auditedItem) {
		// form fields are filled using a map from field ids to values
		HashMap<String, String> formEntries = new HashMap<>();
		formEntries.put("aAuditedItem", auditedItem);
		
		// get the user task "select audited item"
		TaskService taskService = activitiContext.getTaskService();
		Task proposalsTask = taskService.createTaskQuery().taskDefinitionKey("usertask1")
				.singleResult();
		
		// get the list of fields in the form
		List<String> bpmnFieldNames = new ArrayList<>();
		TaskFormData taskFormData = activitiContext.getFormService().getTaskFormData(proposalsTask.getId());
		for (FormProperty fp : taskFormData.getFormProperties()){
			bpmnFieldNames.add(fp.getId());
		}
		
		// build a list of required fields that must be filled
		List<String> requiredFields = new ArrayList<>(
				Arrays.asList("aAuditedItem"));
		
		// make sure that each of the required fields is in the form
		for (String requiredFieldName : requiredFields) {
			assertTrue(bpmnFieldNames.contains(requiredFieldName));
		}
		
		// make sure that each of the required fields was assigned a value
		for (String requiredFieldName : requiredFields) {
			assertTrue(formEntries.keySet().contains(requiredFieldName));
		}
		
		// submit the form (will lead to completing the use task)
		activitiContext.getFormService().submitTaskFormData(proposalsTask.getId(), formEntries);
	}*/
	
	/*private void auditItem(Integer expectedClientId) {
		// get audited item owner
		String auditedItemOwner = (String) activitiContext.getRuntimeService().getVariableLocal(processInstance.getId(), "auditedItemOwner");
		
		//get expected owner address
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) activitiContext.getRuntimeService().getVariableLocal(processInstance.getId(), "accounts");
		String expectedOwner = accounts.get(expectedClientId).getCredentials().getAddress();
		assertTrue(auditedItemOwner.equals(expectedOwner));
	}*/
	@Test
	public void checkRunProcess() {
		// Check process is paused at usertask1
		startProcess();
		assertNotNull(processInstance);
	}
	
		
	@Test
	public void checkRegisterAndPaused() {
		// Check process is paused at usertask1
		startProcess();
		assertNotNull(processInstance);
		
		// get pending tasks
		List<Task> list = activitiContext.getTaskService().createTaskQuery()
				.list();
		
		// assert one pending task
		assertTrue(list.size() == 1);
		
		// assert pending task id
		assertTrue(list.get(0).getTaskDefinitionKey().equals("usertask1"));
	}

	
	@Test
	public void checkArtRegisterRecordedInDB() throws SQLException {
		// Start process. will pause at usertask1
		startProcess();
		
		// Check records in Registered table match records in the Request table
		
		Statement statement;
		ResultSet resultSet;
		
		// First, we load all requested items into a list
		ArrayList<String> requestedItems = new ArrayList<>();
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM ArtRequest");
		while (resultSet.next()) {
			String item = resultSet.getString("item");
			requestedItems.add(item);
		}
		
		// Then, we load all registered items into a list
		ArrayList<String> registeredItems = new ArrayList<>();
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM ArtRegistered");
		while (resultSet.next()) {
			String item = resultSet.getString("item");
			registeredItems.add(item);
		}
		
		// Finally, we make sure that each requested item is registered, and no extra item was registered
		for (String item : registeredItems) {
			assertTrue(registeredItems.contains(item));
		}
		assertTrue(registeredItems.size() == requestedItems.size());
	}
	
	@Test
	public void checkResultsRecordedInDB() throws SQLException {
		// Start process. will pause at usertask1
		startProcess();
		
		// Check records in Registered table match records in the Request table
		
		Statement statement;
		ResultSet resultSet;
		
		// First, we load all requested items into a list
		ArrayList<Integer> queriedItems = new ArrayList<>();
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM Query");
		while (resultSet.next()) {
			Integer queryid = resultSet.getInt("id");
			queriedItems.add(queryid);
		}
		
		// Then, we load all registered items into a list
		ArrayList<Integer> resultsItems = new ArrayList<>();
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM Results");
		while (resultSet.next()) {
			Integer queryid = resultSet.getInt("id");
			resultsItems.add(queryid);
		}
		
		// Finally, we make sure that each requested item is registered, and no extra item was registered
		for (Integer queryid : queriedItems) {
			assertTrue(resultsItems.contains(queryid));
		}
		assertTrue(queriedItems.size() == resultsItems.size());
	}
	
}
