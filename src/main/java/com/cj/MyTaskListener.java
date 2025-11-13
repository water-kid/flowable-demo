package com.cj;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("ww");
    }
}
