package com.cj;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.DataObject;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Test03 {


    ProcessEngine processEngine;

    @Before
    public void before(){
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=Asia/Shanghai&nullCatelogMeansCurrent=true");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root1234");

        configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        processEngine = configuration.buildProcessEngine();
    }


    private static Logger logger = LoggerFactory.getLogger(Test03.class);

    @Test
    public void test(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        String processDefinitionKey = "ask_for_leave";
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        logger.info("definitionId:{},id:{},name:{}", pi.getProcessDefinitionId(), pi.getId(), pi.getName());

    }

    @Test
    public void test01(){
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee("cc")
                .list();

        for (Task task : taskList) {
            logger.info("id:{},assignee:{},name:{}",task.getId(),task.getAssignee(),task.getName());

        }
    }

    @Test
    public void test02(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // 如果流程不存在，，说明结束，，，如果存在，说明流程正在执行
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("xxx").singleResult();

    }

    @Test
    public void test03(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        List<Execution> list = runtimeService.createExecutionQuery().list();
        for (Execution execution : list) {
            // 当前执行实例 活动的节点
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(execution.getId());
            System.out.println(activeActivityIds.size());
            for (String activeActivityId : activeActivityIds) {
                logger.info("activeActivityId:{}", activeActivityId);
            }
        }
    }

    @Test
    public void test06(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for (ProcessInstance processInstance : list) {
            String deleteReason = "想删除";
            runtimeService.deleteProcessInstance(processInstance.getId(),deleteReason);
        }
    }

    @Test
    public void test07(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment().name("test-data-object").key("dataobject")
                .addClasspathResource("ask_for_leave_dataobject.bpmn20.xml").deploy();

        System.out.println("deployment.getId() = " + deployment.getId());
        
        

      
        
    }
    @Test
    public void test08(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById("ask_for_leave:3:10004");


        Map<String, DataObject> dataObjects = runtimeService.getDataObjects(processInstance.getId());
        for (String s : dataObjects.keySet()) {
            DataObject data = dataObjects.get(s);
            logger.info("id:{},name:{},value:{},type:{}",data.getId(),data.getName(),data.getValue(),data.getType());
        }
    }
}
