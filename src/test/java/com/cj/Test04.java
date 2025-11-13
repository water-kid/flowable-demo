package com.cj;

import org.apache.ibatis.annotations.Update;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.common.impl.identity.Authentication;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test04 {

    ProcessEngine processEngine;
    @Before
    public void before(){
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root1234");

        configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

         processEngine = configuration.buildProcessEngine();
    }


    @Test
    public void test04(){

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment().name("test-candicate")
                .addClasspathResource("test-candicate-usertask.bpmn20.xml").key("test-candicate").deploy();

        System.out.println("deployment.getId() = " + deployment.getId());

    }


    private static final Logger logger = LoggerFactory.getLogger(Test04.class);


    @Test
    public void test05(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

//        Authentication.setAuthenticatedUserId("wwww");
        Map<String, Object> map = new HashMap<>();
        map.put("initiator","wwww");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("test-candicate-usertask",map);
        logger.info("id:{},name:{}", pi.getId(), pi.getName());
    }



    @Test
    public void test06(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        List<Execution> list = runtimeService.createExecutionQuery().activityId("sid-3B12C948-9846-4629-81F1-415B7313F035").list();

        for (Execution execution : list) {
            logger.info(execution.getId());

            // 触发往下执行 ===》 接收任务： 等待外部消息或者信号回来触发流程继续，，，比如等待支付结果，等待第三方系统回调灯
            runtimeService.trigger(execution.getId());
        }
    }


    @Test
    public void test07(){

        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskAssignee("ww").list();
        for (Task task : list) {
            logger.info("name:{},assignee:{}", task.getName(), task.getAssignee());
            // 自己处理
//            taskService.complete(task.getId());

            // 委派给别人处理
            taskService.setAssignee(task.getId(),"waterkid");
        }
    }



    @Test
    public void test08(){

        TaskService taskService = processEngine.getTaskService();

        List<Task> taskList = taskService.createTaskQuery().taskAssignee("wwww").list();

        for (Task task : taskList) {
            logger.info("name:{},CreateTime:{}", task.getName(), task.getCreateTime());
        }


    }

    @Test
    public void test09(){
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("wwww").list();
        for (Task task : taskList) {
            logger.info("name:{},CreateTime:{}", task.getName(), task.getCreateTime());
        }
    }


    @Test
    public void test10(){
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();


        // 获取流程的参与者
        List<IdentityLink> links = runtimeService.getIdentityLinksForProcessInstance("45001");

        for (IdentityLink link : links) {
            logger.info("流程参与人：{}",link.getUserId());
        }

    }


    @Test
    public void  test11(){
        TaskService taskService = processEngine.getTaskService();

        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("wwww").list();
        for (Task task : taskList) {
            logger.info(task.getName());

            // 任务认领
            taskService.claim(task.getId(),"wwww");
        }
    }


    @Test
    public void test14(){

        TaskService taskService = processEngine.getTaskService();

        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("zs").list();
        System.out.println(taskList.size());

    }

    @Test
    public void test15(){

        RuntimeService runtimeService = processEngine.getRuntimeService();

        List<IdentityLink> links = runtimeService.getIdentityLinksForProcessInstance("45001");


        TaskService taskService = processEngine.getTaskService();

//        taskService.addCandidateUser();

//        for (IdentityLink link : links) {
//
//            logger.info(link.getUserId());
//        }

//        TaskService taskService = processEngine.getTaskService();
//
//        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("wwww").list();
//
//        for (Task task : taskList) {
//            taskService.claim(task.getId(),"wwww");
//        }

    }


    @Test
    public void test16(){
        TaskService taskService = processEngine.getTaskService();

        List<Task> taskList = taskService.createTaskQuery().taskAssignee("wwww").list();
        for (Task task : taskList) {
            taskService.setAssignee(task.getId(),null);
        }
    }



    public void test17(){
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("xxx").list();
    }

}
