package com.cj;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test06 {

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


        Deployment deployment = repositoryService.createDeployment().name("test-form").key("test-form")
                .addClasspathResource("test-form.bpmn20.xml")
                .addClasspathResource("askleave.html")
                .addClasspathResource("leader_approval.html")
                .deploy();

        System.out.println("deployment = " + deployment.getId());
    }
    @Test
    public void test01() throws IOException {

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("test-form").latestVersion().singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(pd.getId());

        // 绘制图片的生成器
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        // 图片乱码
        InputStream inputStream = generator.generateDiagram(bpmnModel,"png",Collections.emptyList(),Collections.emptyList(), "宋体","宋体","宋体",null,1.0);

        FileUtils.copyInputStreamToFile(inputStream,new File("F:\\code\\learn\\learn-flowable\\flowable-demo\\src\\main\\resources\\images\\1.png"));


        inputStream.close();

    }



    @Test
    public void test02() throws IOException {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("test-form").latestVersion().singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(pd.getId());

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        // 所有已经执行过的活动
        List<String> highLightedActivities = new ArrayList<>();

        // 所有已经执行过的线条
        List<String> highLightedFlows = new ArrayList<>();


//        runtimeService.createActivityInstanceQuery

        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId("7501").list();
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

        FileUtils.copyInputStreamToFile(inputStream,new File("F:\\code\\learn\\learn-flowable\\flowable-demo\\src\\main\\resources\\images\\2.png"));


        inputStream.close();


    }



    @Test
    public void test30(){
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance hi : list) {
            List<HistoricVariableInstance> list1 = historyService.createHistoricVariableInstanceQuery().processInstanceId(hi.getId()).list();
            System.out.println("list1 = " + list1);
        }
    }
}
