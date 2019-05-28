package com.cav.googlecloud.task.service.taskservice;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cav.googlecloud.task.service.model.Fund;
import com.google.appengine.api.LifecycleManager;
import com.google.appengine.api.taskqueue.LeaseOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueStatistics;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.apphosting.api.ApiProxy.ApiDeadlineExceededException;

public abstract class AbstractTaskService {
	
	protected Queue addPullQueue = null;
	protected Queue addPushQueue = null;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractTaskService.class);
	
	//Pull
	protected TaskOptions mapFundToTaskParams(Fund fund, Method method){
		return TaskOptions.Builder.withMethod(method)
        .tag("FundTask")
        .param("ClientId", fund.getClientId())
        .param("FundId", fund.getFundId())
        .param("FundName", fund.getFundName());
	}
	
	protected TaskOptions mapFundToTaskPayLoad(Fund fund, Method method){
		return TaskOptions.Builder.withMethod(method)
        .tag("FundTask")
        .payload(fund.toString());
	}
	
	protected List <Fund> fundWorkerPullParms(){
		List <Fund> funds = new ArrayList <Fund> ();
		QueueStatistics stats = addPullQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		LeaseOptions options = buildOptionsPull();
		List<TaskHandle> tasks = addPullQueue.leaseTasks(options);
		for(TaskHandle task: tasks){
			Fund fund = mapTaskToFundParam(task);
			funds.add(fund);
		}
		return funds;
	}
	
	protected List <Fund> fundWorkerPullPayload(){
		List <Fund> funds = new ArrayList <Fund> ();
		QueueStatistics stats = addPullQueue.fetchStatistics();
		logger.info("QUEUE STATS Number of Tasks "+stats.getNumTasks());
		LeaseOptions options = buildOptionsPull();
		List<TaskHandle> tasks = addPullQueue.leaseTasks(options);
		for(TaskHandle task: tasks){
			funds.add(mapTaskToFundPayload(task));
		}
		return funds;
	}
	
	//Push
	protected TaskOptions mapFundToTaskParamsPush(Fund fund, Method method){
		logger.info("Map Fund To Task for fund "+fund.toString()+" method "+method.toString());
		return TaskOptions.Builder.withMethod(method)
        .param("ClientId", fund.getClientId())
        .param("FundId", fund.getFundId())
        .param("FundName", fund.getFundName());
	}
	
	protected TaskOptions mapFundToTaskPayLoadPush(Fund fund, Method method){
		return TaskOptions.Builder.withMethod(method)
        .payload(fund.toString());
	}

	
	protected List <Fund> fundWorkerPushParms(){
		List <Fund> funds = new ArrayList <Fund> ();
		//LeaseOptions options = buildOptionsPush();
		List<TaskHandle> tasks = PushNotificationWorker();
		for(TaskHandle task: tasks){
			Fund fund = mapTaskToFundParam(task);
			funds.add(fund);
		}
		return funds;
	}
	
	protected List<Fund> fundWorkerPushPayload(){
		List <Fund> funds = new ArrayList <Fund> ();
		logger.info("FundWorker Get Push Payload");
		List<TaskHandle> tasks = PushNotificationWorker();
		for(TaskHandle task: tasks){
			Fund fund = mapTaskToFundPayload(task);
			funds.add(fund);
		}
		return funds;
	}
	
	private Fund mapTaskToFundParam(TaskHandle th){
		logger.info("TASK HANDLE PARAM : "+th.toString());
		Fund fund = new Fund();
		try {
			List<Map.Entry<String, String>> entries = th.extractParams();
			for(Map.Entry <String, String> entry : entries){
				logger.info("Entry "+entry.getKey()+" : "+entry.getValue());
				if(entry.getKey().equals("ClientId")){
					fund.setClientId(entry.getValue());
				} else if(entry.getKey().equals("FundId")){
					fund.setFundId(entry.getValue());
				} else if(entry.getKey().equals("FundName")){
					fund.setFundName(entry.getValue());
				}
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
		 String values = pl.substring(pl.indexOf('[') + 1, pl.indexOf(']'));
		 String plA[]= values.split(",");
		 fund.setClientId(getValue(plA[0]));
		 fund.setFundId(getValue(plA[1]));
		 fund.setFundName(getValue(plA[2]));
		return fund;
	}
	
	
	private LeaseOptions buildOptionsPull(){
		int count = 2;
        Long leaseDuration = 60L;
		return LeaseOptions.Builder
	            .withTag("FundTask")
	            .countLimit(count)
	            .leasePeriod(leaseDuration, TimeUnit.SECONDS);
	}
	
	private LeaseOptions buildOptionsPush(){
		int count = 2;
        Long leaseDuration = 60L;
		return LeaseOptions.Builder
	            .withCountLimit(count)
	            .leasePeriod(leaseDuration, TimeUnit.SECONDS);
	}
	
	private String getValue(String payload){
		logger.info("Payload: "+payload);
		String[] valArr = payload.split("=");
		return valArr[1];
		
	}
	
	//New Code

	private List<TaskHandle> PushNotificationWorker(){
		List<TaskHandle> tasks = leaseTasks(addPushQueue);
		return tasks;
	}
	
	private List<TaskHandle> leaseTasks(Queue addPushQueue) {
		logger.info("Lease Tasks Push on Queue "+addPushQueue.getQueueName());
	    List<TaskHandle> tasks;
	    for (int attemptNo = 1; !LifecycleManager.getInstance().isShuttingDown(); attemptNo++) {
	      try {
	    	  logger.info("LeaseTasks get push Tasks ");
	    	  tasks = addPushQueue.leaseTasks(30, TimeUnit.MINUTES, 100);
	    	  return tasks;
	      } catch (TransientFailureException e) {
	    	  logger.info("TransientFailureException when leasing tasks from queue '"
	            + addPushQueue.getQueueName() + "'");
	      } catch (ApiDeadlineExceededException e) {
	    	  logger.info("ApiDeadlineExceededException when when leasing tasks from queue '"
	            + addPushQueue.getQueueName() + "'");
	      }
	      if (!backOff(attemptNo)) {
	        return null;
	      }
	    }
	    return null;
	  }
	
	private boolean backOff(int attemptNo) {
	    // Exponential back off between 2 seconds and 64 seconds with jitter 0..1000 ms.
	    attemptNo = Math.min(6, attemptNo);
	    int backOffTimeInSeconds = 1 << attemptNo;
	    try {
	      Thread.sleep(backOffTimeInSeconds * 1000 + (int) (Math.random() * 1000));
	    } catch (InterruptedException e) {
	      return false;
	    }
	    return true;
	  }
}
