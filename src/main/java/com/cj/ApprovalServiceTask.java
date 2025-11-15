package com.cj;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * 请假通过的 执行这个服务类
 */
public class ApprovalServiceTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Map<String, Object> variables = delegateExecution.getVariables();
        Object name = variables.get("name");

        Object days = variables.get("days");

        System.out.println(name+"请假通过："+days);
    }
}
