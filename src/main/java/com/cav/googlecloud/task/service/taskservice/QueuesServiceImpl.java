package com.cav.googlecloud.task.service.taskservice;


import org.springframework.stereotype.Service;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

@Service
public class QueuesServiceImpl implements QueuesService{

	private static final String ADD_FUND_PULL = "addFundPull";
    private static final String ADD_FUND_PUSH = "addFundPush";
    
    @Override
	public Queue defaultQueue() {
		return QueueFactory.getDefaultQueue();
	}
	@Override
	public Queue fundPullQueue() {
		return QueueFactory.getQueue(ADD_FUND_PULL);
	}
	@Override
	public Queue fundPushQueue() {
		return QueueFactory.getQueue(ADD_FUND_PUSH);
	}
}
