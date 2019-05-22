package com.cav.googlecloud.task.service.taskservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

@Service
public class QueuesServiceImpl implements QueuesService{

	private static final String ADD_QUEUE_NAME = "add";
    private static final String DELETE_QUEUE_NAME = "delete";
    
    @Override
	public Queue defaultQueue() {
		return QueueFactory.getDefaultQueue();
	}
	@Override
	public Queue addQueue() {
		return QueueFactory.getQueue(ADD_QUEUE_NAME);
	}
	@Override
	public Queue deleteQueue() {
		return QueueFactory.getQueue(DELETE_QUEUE_NAME);
	}
}
