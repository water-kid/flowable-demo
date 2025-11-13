package com.cj;


import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test01 {


    ProcessEngine processEngine = null;
    ProcessEngineConfiguration configuration = null;




    @Before
    public void before(){
        // 核心对象
        configuration = new StandaloneProcessEngineConfiguration();

        // 配置数据库相关信息
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        // nullCatelogMeansCurrent :  当 catalog参数为null时，，，jdbc驱动如何处理，，，当设置为true的时候，，表示将 null catalog 解释为 “当前连接的数据库”，，当为false的时候，，将null catalog解释为“所有数据库”
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root1234");

        // 如果数据库中的表结构不存在，，就新建
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);


        // 获取对象
         processEngine = configuration.buildProcessEngine();

    }



    @Test
    public void testDeploy(){
        // 获取对象
        ProcessEngine processEngine = configuration.buildProcessEngine();

        // 2. 获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();


        // 完成流程的部署操作
        Deployment deployment = repositoryService.createDeployment()
                // 关联要部署的流程文件
                .addClasspathResource("holiday-request.bpmn20.xml")
                .name("请假流程")
                .deploy();


        String id = deployment.getId();
        System.out.println("id = " + id);
        System.out.println("deployment = " + deployment.getName());


    }
    @Test
    public void test02(){

        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<>();

        variables.put("name","cc");
        variables.put("age",20);


        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
    }

    @Test
    public void test03(){
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();

        System.out.println("you have"+tasks.size()+" tasks");
    }


}
