package com.iweb.panda.test;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 用户测试
 * 
 * @author chenlj
 * @Date 2016年3月24日 下午6:21:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class ActivitiUserUnit {
	private static final Logger logger = LoggerFactory.getLogger(ActivitiUserUnit.class);
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
	@Resource
	private IdentityService identityService;

	/**
	 * 获取用户列表
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void getUserList() {
		UserQuery userQuery = identityService.createUserQuery();
		List<User> users = userQuery.list();
		for (User user : users) {
			_printUser(user);
		}
	}

	private void _printUser(User user) {
		logger.info("id:{},firstName:{},lastName:{},email:{},password:{}",
				new Object[] { user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword() });
	}

	/**
	 * 新增用户，无头像
	 * 
	 * @author chenlj
	 * @Date 2016 下午7:47:20
	 */
	@Test
	public void addUser() {
		String id = "1";
		User user = identityService.newUser(id);
		user.setFirstName("小");
		user.setLastName("张");
		user.setPassword("123");
		identityService.saveUser(user);
		System.out.println("create user success!");
	}

	/**
	 * 根据id获取用户
	 * 
	 * @author chenlj
	 * @Date 2016 下午7:47:20
	 */
	@Test
	public void getUserById() {
		String id = "1";
		UserQuery userQuery = identityService.createUserQuery();
		User user = userQuery.userId(id).singleResult();
		_printUser(user);
	}
}
