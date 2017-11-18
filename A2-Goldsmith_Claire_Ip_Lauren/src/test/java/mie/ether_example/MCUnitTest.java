package mie.ether_example;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.task.Task;
import org.junit.BeforeClass;
import org.junit.Test;

public class MCUnitTest extends LabBaseUnitTest {
	
	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/MC.bpmn";
	}
	
	private void startProcess() {	
		RuntimeService runtimeService = activitiContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("myProcess");
	}
	
	@Test
	public void runProcess() {
		startProcess();
	}
	
}
