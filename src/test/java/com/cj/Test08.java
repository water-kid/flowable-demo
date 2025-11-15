package com.cj;

import org.flowable.engine.*;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Test08 {


    ProcessEngine processEngine;

    TaskService taskService;
    RuntimeService runtimeService;
    RepositoryService repositoryService;
    HistoryService historyService;

    FormService formService;

    @Before
    public void before(){

        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();

        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root1234");
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true");

        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);


        processEngine = configuration.buildProcessEngine();

        runtimeService = processEngine.getRuntimeService();
        repositoryService = processEngine.getRepositoryService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        formService = processEngine.getFormService();



    }

    @Test
    public void test01(){


        Deployment deployment = repositoryService.createDeployment().name("test-flowable").key("test-flowable")
                .addClasspathResource("test-whole-flowable.bpmn20.xml")
                .addClasspathResource("askleave.html")
                .addClasspathResource("leader_approval.html")
                .deploy();

        System.out.println("deployment = " + deployment.getId());
    }


    @Test
    public void test02(){

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey("test-whole-flowable").singleResult();

        String startFormKey = formService.getStartFormKey(pd.getId());
        Object renderedStartForm = formService.getRenderedStartForm(pd.getId());
        System.out.println("startFormData = " + renderedStartForm);
        System.out.println("startFormKey = " + startFormKey);


        Map<String, String> vars = new HashMap<>();
        vars.put("startTime","2025-11-15");
        vars.put("endTime","2025-11-25");
        vars.put("days","10");
        vars.put("reason","xxxx");

        vars.put("approvalUser","cc");

        formService.submitStartFormData(pd.getId(),vars);
    }

    @Test
    public void test03(){
        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        System.out.println("task.getId() = " + task.getId());

        Object renderedTaskForm = formService.getRenderedTaskForm(task.getId());

        System.out.println("renderedTaskForm = " + renderedTaskForm);

        Map<String, String> vars = new HashMap<>();
        vars.put("reason","我想请假");


        taskService.setVariable(task.getId(),"approve",true);

        formService.submitTaskFormData(task.getId(),vars);


    }





}
