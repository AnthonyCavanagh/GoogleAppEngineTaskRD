package com.cav.googlecloud.task.service.taskservice;

import com.google.appengine.api.taskqueue.Queue;

public interface QueuesService {

	Queue defaultQueue();
	Queue fundPullQueue();
	Queue fundPushQueue();
}
