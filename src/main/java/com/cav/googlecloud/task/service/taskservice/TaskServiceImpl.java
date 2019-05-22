package com.cav.googlecloud.task.service.taskservice;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cav.googlecloud.task.service.model.Fund;
import com.cav.googlecloud.task.service.model.Funds;
import com.google.appengine.api.taskqueue.LeaseOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueStatistics;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;


@Service
public class TaskServiceImpl implements TaskService{
	
	@Autowired
	private QueuesService queueService;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	Queue addQueue = null;

	List <TaskHandle> taskHandles = new ArrayList<TaskHandle>();
	@Override
	public void addFundsParams(Funds funds) {
		addQueue = queueService.addQueue();
		QueueStatistics stats = addQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		for(Fund fund : funds.getFunds()){
			 TaskHandle task = addQueue.add(mapFundToTaskParams(fund));
			 logger.info("TASK HANDLE : "+task.toString());
			 taskHandles.add(task);
		}
		stats = addQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
	}
	
	@Override
	public void addFundsPayload(Funds funds) {
		addQueue = queueService.addQueue();
		QueueStatistics stats = addQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		for(Fund fund : funds.getFunds()){
			 TaskHandle task = addQueue.add(mapFundToTaskPayLoad(fund));
			 taskHandles.add(task);
		}
		stats = addQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
	}

	@Override
	public void deleteFunds(Funds funds) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Funds getFundsParms() {
		Funds funds = new Funds();
		fundWorkerParms();
		return funds;
	}
	
	@Override
	public Funds getFundsPayload() {
		Funds funds = new Funds();
		fundWorkerPayload();
		return funds;
	}
	
	private void fundWorkerParms(){
		QueueStatistics stats = addQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		LeaseOptions options = buildOptions();
		List<TaskHandle> tasks = addQueue.leaseTasks(options);
		for(TaskHandle task: tasks){
			mapTaskToFundParam(task);
		}
	}
	
	private void fundWorkerPayload(){
		QueueStatistics stats = addQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		LeaseOptions options = buildOptions();
		List<TaskHandle> tasks = addQueue.leaseTasks(options);
		for(TaskHandle task: tasks){
			mapTaskToFundPayload(task);
		}
	}
	
	private Fund mapTaskToFundParam(TaskHandle th){
		logger.info("TASK HANDLE PARAM : "+th.toString());
		Fund fund = new Fund();
		try {
			List<Map.Entry<String, String>> entries = th.extractParams();
			for(Map.Entry <String, String> entry : entries){
				logger.info("Entry "+entry.getKey()+" : "+entry.getValue());
			}
		} catch (UnsupportedEncodingException | UnsupportedOperationException e) {
			e.printStackTrace();
		}
		return fund;
	}
	
	private Fund mapTaskToFundPayload(TaskHandle th){
		logger.info("TASK HANDLE PAYLOAD: "+th.toString());
		Fund fund = new Fund();
		 byte[] payload = th.getPayload();
		 String pl = new String(payload);
		 logger.info("PAY LOAD : "+pl);
		return fund;
	}
	
	
	private TaskOptions mapFundToTaskParams(Fund fund){
		return TaskOptions.Builder.withMethod(Method.PULL)
        .tag("FundTask")
        .param("ClientId", fund.getClientId())
        .param("FundId", fund.getFundId())
        .param("FundName", fund.getFundName());
	}
	
	private TaskOptions mapFundToTaskPayLoad(Fund fund){
		return TaskOptions.Builder.withMethod(Method.PULL)
        .tag("FundTask")
        .payload(fund.toString());
	}

	
	private LeaseOptions buildOptions(){
		int count = 2;
        Long leaseDuration = 60L;
		return LeaseOptions.Builder
	            .withTag("FundTask")
	            .countLimit(count)
	            .leasePeriod(leaseDuration, TimeUnit.SECONDS);
	}

}
