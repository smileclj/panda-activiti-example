package com.iweb.panda.test;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 部署测试
 * 
 * @author chenlj
 * @Date 2016年3月24日 下午6:21:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class ActivitiDeploymentUnit {
	private static final Logger logger = LoggerFactory.getLogger(ActivitiDeploymentUnit.class);
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
	 * 获取部署列表
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void getDeploymentList() {
		DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
		List<Deployment> deployments = deploymentQuery.list();
		for (Deployment deployment : deployments) {
			_printDeployment(deployment);
		}
	}

	private void _printDeployment(Deployment deployment) {
		logger.info("id:{},name:{},deploymentTime:{},category:{},tenantId:{}",
				new Object[] { deployment.getId(), deployment.getName(), deployment.getDeploymentTime(), deployment.getCategory(), deployment.getTenantId() });
	}

	/**
	 * 根据id删除部署
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:20:08
	 */
	@Test
	public void removeDeploymentById() {
		String id = "2501";
		// 级联删除
		repositoryService.deleteDeployment(id, true);
	}

	/**
	 * 根据id获取部署
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:20:21
	 */
	@Test
	public void getDeploymentById() {
		String id = "1";
		DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
		Deployment deployment = deploymentQuery.deploymentId(id).singleResult();
		_printDeployment(deployment);
	}
}
