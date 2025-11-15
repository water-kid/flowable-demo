package com.cj;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test07 {
    ProcessEngine processEngine;

    RepositoryService repositoryService;

    RuntimeService runtimeService;
    TaskService taskService;

    @Before
    public void before(){
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root1234");



        configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);






        processEngine = configuration.buildProcessEngine();

        repositoryService = processEngine.getRepositoryService();

        runtimeService = processEngine.getRuntimeService();

        taskService = processEngine.getTaskService();
    }

    @Test
    public void test20(){


        Deployment deployment = repositoryService.createDeployment().name("test-flowable").key("test-flowable")
                .addClasspathResource("test-whole-flowable.bpmn20.xml")
                .addClasspathResource("askleave.html")
                .addClasspathResource("leader_approval.html")
                .deploy();

        System.out.println("deployment = " + deployment.getId());
    }

    @Test
    public void test21(){

//        runtimeService.

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey("test-whole-flowable").singleResult();

        FormService formService = processEngine.getFormService();

        // 获取表单名字
        String startFormKey = formService.getStartFormKey(pd.getId());
        // 获取真实表单
        Object renderedStartForm = formService.getRenderedStartForm(pd.getId());
        System.out.println("renderedStartForm = " + renderedStartForm);


        Map<String, String> vars = new HashMap<>();
        vars.put("days","10");
        vars.put("reason","我要请假");
        vars.put("startTime","2025-11-15");
        vars.put("endTime","2025-11-25");


        vars.put("approvalUser","admin");


        ProcessInstance pi = formService.submitStartFormData(pd.getId(), vars);



    }

    @Test
    public void test22(){
        Task task = taskService.createTaskQuery().taskAssignee("admin").singleResult();

        FormService formService = processEngine.getFormService();
        Object taskForm = formService.getRenderedTaskForm(task.getId());
        System.out.println("taskForm = " + taskForm);

        taskService.setVariable(task.getId(),"approve",true);

        Map<String, String> vars = new HashMap<>();

        vars.put("days","20");
        vars.put("reason","呵呵");


        formService.submitTaskFormData(task.getId(),vars);
    }
    @Test
    public void test02() throws IOException {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("test-whole-flowable").latestVersion().singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(pd.getId());

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        // 所有已经执行过的活动
        List<String> highLightedActivities = new ArrayList<>();

        // 所有已经执行过的线条
        List<String> highLightedFlows = new ArrayList<>();


//        runtimeService.createActivityInstanceQuery

        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId("17501").list();
        for (HistoricActivityInstance ai : list) {

            // todo 为什么历史活动节点表里面 没有连线
            if (ai.getActivityType().equals("sequenceFlow")){
                highLightedFlows.add(ai.getActivityId());
            }else{
                highLightedActivities.add(ai.getActivityId());
            }
        }


        // 图片乱码
        InputStream inputStream = generator.generateDiagram(bpmnModel,"png",highLightedActivities,highLightedFlows, "宋体","宋体","宋体",null,1.0);

        FileUtils.copyInputStreamToFile(inputStream,new File("F:\\code\\learn\\learn-flowable\\flowable-demo\\src\\main\\resources\\images\\3.png"));


        inputStream.close();


    }
}
