package com.iweb.panda.activiti;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 任务测试
 * 
 * @author chenlj
 * @Date 2016年3月24日 下午6:21:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class ActivitiTaskUnit {
	private static final Logger logger = LoggerFactory.getLogger(ActivitiTaskUnit.class);
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
	 * 启动流程实例
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void startProcess() {
		String processDefinitionKey = "test1";
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
		_printProcessInstance(processInstance);
	}

	/**
	 * 获取流程实例列表
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void getProcessList() {
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
		for (ProcessInstance processInstance : processInstances) {
			_printProcessInstance(processInstance);
		}
	}

	private void _printProcessInstance(ProcessInstance processInstance) {
		logger.info(
				"ProcessInstanceId:{},Name:{},ProcessDefinitionId:{},ProcessDefinitionName:{},ProcessDefinitionKey:{},DeploymentId:{},BusinessKey:{},isSuspended:{}",
				new Object[] { processInstance.getProcessInstanceId(), processInstance.getName(), processInstance.getProcessDefinitionId(),
						processInstance.getProcessDefinitionName(), processInstance.getProcessDefinitionKey(), processInstance.getDeploymentId(),
						processInstance.getBusinessKey(), processInstance.isSuspended() });
	}

	/**
	 * 删除流程实例
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void removeProcess() {
		// runtimeService.deleteProcessInstance(paramString1, paramString2);
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
		for (ProcessInstance processInstance : processInstances) {
			_printProcessInstance(processInstance);
		}
	}
}
