package com.cj;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Test02 {
    ProcessEngine processEngine;

    @Before
    public void before(){
        ProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("root1234");
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");

        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);


        processEngine = configuration.buildProcessEngine();
    }

    @Test
    public void test01(){
        RepositoryService repositoryService = processEngine.getRepositoryService();


        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            System.out.println("deployment = " + deployment);


            repositoryService.deleteDeployment(deployment.getId());
        }
    }

    @Test
    public void test(){

        RepositoryService repositoryService = processEngine.getRepositoryService();


        Deployment deployment = repositoryService.createDeployment()
                .name("请假").key("leave").category("请假分类").addClasspathResource("ask_for_leave.bpmn20.xml").deploy();


        System.out.println("deployment.getId() = " + deployment.getId());

    }

    private static final Logger logger = LoggerFactory.getLogger(Test02.class);
    @Test
    public void test02(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                // 根据xml中的id 去找
                .processDefinitionKey("ask_for_leave")
                // 按照版本号排序
                .orderByProcessDefinitionKey()
                .desc() // 这个desc好像没什么用
                .list();

        for (ProcessDefinition pd : list) {
            logger.info("id:{},name:{},version:{},category:{}",pd.getId(),pd.getName(),pd.getVersion(),pd.getCategory());
        }
    }



    public void test03(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment d : list) {
            List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().deploymentId(d.getId()).list();
            for (ProcessDefinition pd : pdList) {
                logger.info("id:{},name:{},version:{},category:{}",pd.getId(),pd.getName(),pd.getVersion(),pd.getCategory());
            }

        }
    }


    public void test04(){
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Deployment> list = repositoryService.createDeploymentQuery().list();

        for (Deployment deployment : list) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }



}
