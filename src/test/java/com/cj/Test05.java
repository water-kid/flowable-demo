package com.cj;

import org.flowable.engine.*;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test05 {

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
    public void test01(){

        Deployment deployment = repositoryService.createDeployment().name("test相容网关").key("test-gateway")
                .addClasspathResource("test-gateway.bpmn20.xml")
                .deploy();
        System.out.println("deployment = " + deployment.getId());
    }


    @Test
    public void test02(){

        // 启动的时候设置变量，，这个流程变量会被存入两个地方，，运行时的变量表，和历史变量表 act_ru_variable
        HashMap<String, Object> map = new HashMap<>();
        map.put("money",1000);
        map.put("days",10);
        map.put("reason","我要去玩");
        runtimeService.startProcessInstanceByKey("test-gateway",map);
    }

    @Test
    public void test03(){

        String executionId = "57501";
// 获取指定流程实例的  变量
        Object money = runtimeService.getVariable(executionId, "money");
        System.out.println("money = " + money);

        Map<String, Object> variables = runtimeService.getVariables(executionId);
        for (String s : variables.keySet()) {
            System.out.println(s+":"+variables.get(s));
        }

    }


    @Test
    public void test06(){

        List<Task> taskList = taskService.createTaskQuery().taskAssignee("zs").list();

        for (Task task : taskList) {
            System.out.println("task = " + task);

            // 也可以通过任务id 去查全局的流程变量
            Map<String, Object> variables = taskService.getVariables(task.getId());
            System.out.println("variables = " + variables);


            taskService.setVariable(task.getId(),"result","我不同意");

            Map<String, Object> variables02 = taskService.getVariables(task.getId());
            System.out.println("variables = " + variables02);



            // 完成任务的时候，，可以设置一个变量===》
            // 1.记录任务处理结果  2.传递数据给后续的节点  3，记录业务数据 4.流程结束时数据归档
            // 如果后面有网关，， 可以通过变量控制流程的走向
            // 多个审批结果汇总
        }
    }




    @Test
    public void test07(){


        Task task = taskService.createTaskQuery().taskAssignee("zs").singleResult();

        // 设置本地的流程变量，，，会携带task_id
        // 本地流程变量，，在完成任务之后，跟这个任务相关的流程变量就会被删掉（ACT_RU_VARIABLE表中）
        taskService.setVariableLocal(task.getId(),"username","cnm");
    }

    @Test
    public void test08(){
        Task task = taskService.createTaskQuery().taskAssignee("zs").singleResult();

        Map<String, Object> variables = taskService.getVariables(task.getId());
        System.out.println("variables = " + variables);
    }

    @Test
    public void test09(){
        // 为执行实例设置本地变量 ===> 执行实例也能设置本地的变量，，，只有属于这条执行实例的 任务，，才能读到这些本地变量
        Task task = taskService.createTaskQuery().taskAssignee("zs").singleResult();


    }

    @Test
    public void test10(){
        runtimeService.createProcessInstanceBuilder()
                // 设置临时变量，，在复杂的系统中，可能需要一些中间计算的结果
                .transientVariables(new HashMap<>()).start();


        // 完成任务的时候也可以设置一些临时变量
        Map<String, Object> transientVariables = new HashMap<>();
        Task task = taskService.createTaskQuery().taskAssignee("zs").singleResult();
        taskService.complete(task.getId(),null,transientVariables);



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
    public void test21(){

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey("test-form").singleResult();

        FormService formService = processEngine.getFormService();

        String startFormKey = formService.getStartFormKey(pd.getId());


        // 查询启动节点的表单内容  ===》 前段拉取到表单内容，，就可以提交数据
        Object renderedStartForm = formService.getRenderedStartForm(pd.getId());

        System.out.println("startFormKey = " + startFormKey);
        System.out.println("renderedStartForm = " + renderedStartForm);
    }


    @Test
    public void test22(){
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("test-form").latestVersion().singleResult();
        Map<String, String> map = new HashMap<>();
        map.put("reason","我想玩两天");
        map.put("days","3");
        map.put("startTime","2022-1-1");
        map.put("endTime","2022-1-1");


        FormService formService = processEngine.getFormService();
        ProcessInstance pi = formService.submitStartFormData(pd.getId(), map);


    }

    @Test
    public void test23(){
        Task task = taskService.createTaskQuery().taskAssignee("zs").singleResult();
        System.out.println("task.getId() = " + task.getId());
        FormService formService = processEngine.getFormService();
        Object renderedTaskForm = formService.getRenderedTaskForm(task.getId());
        System.out.println("renderedTaskForm = " + renderedTaskForm);

        // 审批人 获取到这个form，，可以修改这些表单

        Map<String, String> vars = new HashMap<>();
        vars.put("startTime","xxx");
        vars.put("endTime","yyy");
        vars.put("reason","玩尼玛");


        // 可以使用 submitTaskFormData进行审批，，也可以使用complete 进行审批
        formService.submitTaskFormData(task.getId(),vars);

//        taskService.complete(task.getId());

    }

    @Test

    public void test24(){
        List<Task> taskList = taskService.createTaskQuery().taskAssignee("ls").list();
        for (Task task : taskList) {
            FormService formService = processEngine.getFormService();
            Object renderedTaskForm = formService.getRenderedTaskForm(task.getId());
            System.out.println("renderedTaskForm = " + renderedTaskForm);
        }

    }

}
