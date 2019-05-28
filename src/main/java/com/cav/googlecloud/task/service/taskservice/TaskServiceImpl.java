package com.cav.googlecloud.task.service.taskservice;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cav.googlecloud.task.service.model.Fund;
import com.cav.googlecloud.task.service.model.Funds;

import com.google.appengine.api.taskqueue.QueueStatistics;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;


@Service
public class TaskServiceImpl extends AbstractTaskService implements TaskService{
	
	@Autowired
	private QueuesService queueService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	

	List <TaskHandle> taskHandles = new ArrayList<TaskHandle>();
	@Override
	public void addFundsParams(Funds funds) {
		addPullQueue = queueService.fundPullQueue();
		QueueStatistics stats = addPullQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		for(Fund fund : funds.getFunds()){
			 TaskHandle task = addPullQueue.add(mapFundToTaskParams(fund, Method.PULL));
			 logger.info("TASK HANDLE : "+task.toString());
			 taskHandles.add(task);
		}
		stats = addPullQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
	}
	
	@Override
	public void addFundsPayload(Funds funds) {
		addPullQueue = queueService.fundPullQueue();
		QueueStatistics stats = addPullQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		for(Fund fund : funds.getFunds()){
			 TaskHandle task = addPullQueue.add(mapFundToTaskPayLoad(fund, Method.PULL));
			 taskHandles.add(task);
		}
		stats = addPullQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
	}
	
	@Override
	public void addFundsParamPush(Funds funds) {
		addPushQueue = queueService.fundPushQueue();
		for(Fund fund : funds.getFunds()){
			 TaskHandle task = addPushQueue.add(mapFundToTaskPayLoadPush(fund, Method.POST));
			 taskHandles.add(task);
		}
	}
	
	@Override
	public void addFundsPayloadPush(Funds funds) {
		logger.info("Add Funds PayLoad Push");
		addPushQueue = queueService.fundPushQueue();
		for(Fund fund : funds.getFunds()){
			 TaskOptions option = mapFundToTaskParamsPush(fund, Method.POST);
			 TaskHandle task = addPushQueue.add(option);
			 taskHandles.add(task);
		}
	}

	@Override
	public Funds getFundsParmsPull() {
		Funds funds = new Funds();
		funds.getFunds().addAll(fundWorkerPullParms());
		return funds;
	}
	
	@Override
	public Funds getFundsPayloadPull() {
		Funds funds = new Funds();
		funds.getFunds().addAll(fundWorkerPullPayload());
		return funds;
	}

	@Override
	public Funds getFundsParmsPush() {
		Funds funds = new Funds();
		funds.getFunds().addAll(fundWorkerPushParms());
		return funds;
	}
	
	@Override
	public Funds getFundsPayloadPush() {
		Funds funds = new Funds();
		funds.getFunds().addAll(fundWorkerPushPayload());
		return funds;
	}
	
	

}
