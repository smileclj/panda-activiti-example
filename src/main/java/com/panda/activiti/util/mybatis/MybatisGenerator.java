package com.panda.activiti.util.mybatis;

import org.mybatis.generator.api.ShellRunner;

public class MybatisGenerator {

    public static void main(String[] args) {
        String config = MybatisGenerator.class.getClassLoader().getResource("mybatis/generatorConfig.xml").getFile();
        String[] arg = { "-configfile", config };
        ShellRunner.main(arg);
    }
}
