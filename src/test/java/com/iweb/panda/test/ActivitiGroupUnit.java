package com.iweb.panda.test;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 群组测试
 * 
 * @author chenlj
 * @Date 2016年3月24日 下午6:21:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class ActivitiGroupUnit {
	private static final Logger logger = LoggerFactory.getLogger(ActivitiGroupUnit.class);
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
	 * 获取群组列表
	 * 
	 * @author chenlj
	 * @Date 2016 下午6:11:04
	 */
	@Test
	public void getUserList() {
		GroupQuery groupQuery = identityService.createGroupQuery();
		List<Group> groups = groupQuery.list();
		for (Group group : groups) {
			_printGroup(group);
		}
	}

	private void _printGroup(Group group) {
		logger.info("id:{},name:{},type:{}", new Object[] { group.getId(), group.getName(), group.getType() });
	}

	/**
	 * 新增群组
	 * 
	 * @author chenlj
	 * @Date 2016 下午7:47:20
	 */
	@Test
	public void addGroup() {
		String id = "1";
		Group group = identityService.newGroup(id);
		group.setName("群组1");
		group.setType("类型1");
		identityService.saveGroup(group);
		System.out.println("create user success!");
	}

	/**
	 * 根据id获取群组
	 * 
	 * @author chenlj
	 * @Date 2016 下午7:471:20
	 */
	@Test
	public void getGroupById() {
		String id = "1";
		Group group = identityService.createGroupQuery().groupId(id).singleResult();
		_printGroup(group);
	}

	/**
	 * 添加群组成员
	 * 
	 * @author chenlj
	 * @Date 2016 下午7:47:20
	 */
	@Test
	public void addGroupMember() {
		String userId = "1";
		String groupId = "1";
		identityService.createMembership(userId, groupId);
	}

	/**
	 * 删除群组成员
	 * 
	 * @author chenlj
	 * @Date 2016 下午7:47:20
	 */
	@Test
	public void removeGroupMember() {
		String userId = "1";
		String groupId = "1";
		identityService.deleteMembership(userId, groupId);
	}

	/**
	 * 根据id查找组内成员
	 * 
	 * @author chenlj
	 * @Date 2016 下午8:12:19
	 */
	@Test
	public void getGroupMembers() {
		String groupId = "1";
		List<User> users = identityService.createUserQuery().memberOfGroup(groupId).list();
		for (User user : users) {
			_printUser(user);
		}
	}

	private void _printUser(User user) {
		logger.info("id:{},firstName:{},lastName:{},email:{},password:{}",
				new Object[] { user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword() });
	}
}
