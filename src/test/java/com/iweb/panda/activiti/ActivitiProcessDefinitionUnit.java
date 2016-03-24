package com.iweb.panda.activiti;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 流程定义测试
 * 
 * @author chenlj
 * @Date 2016年3月24日 下午6:21:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class ActivitiProcessDefinitionUnit {
	private static final Logger logger = LoggerFactory.getLogger(ActivitiProcessDefinitionUnit.class);
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private ManagementService managementService;

	/**
	 * 获取流程定义列表
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void getProcessDefinitionList() {
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
		for (ProcessDefinition processDefinition : processDefinitions) {
			_printProcessDefinition(processDefinition);
		}
	}

	private void _printProcessDefinition(ProcessDefinition processDefinition) {
		logger.info("id:{},name:{},key:{},deploymentId:{},version:{}",
				new Object[] { processDefinition.getId(), processDefinition.getName(), processDefinition.getKey(), processDefinition.getDeploymentId(),
						processDefinition.getVersion() });
	}

}
