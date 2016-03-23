package com.panda.activiti.util.activiti;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ActivitiDbCreate {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
		System.out.println("beanNames==>");
		StringBuilder sb = new StringBuilder();
		for (String beanName : context.getBeanDefinitionNames()) {
			sb.append(beanName + "\r\n");
		}
		System.out.println(sb.toString());
		context.close();
		System.out.println("Activiti Create Success!");
	}
}
