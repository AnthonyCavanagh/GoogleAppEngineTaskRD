package com.cav.googlecloud.task.service.taskservice;

import com.cav.googlecloud.task.service.model.Funds;

public interface TaskService {

	void addFundsParams(Funds funds);
	void addFundsPayload(Funds funds);
	
	void addFundsParamPush(Funds funds);
	void addFundsPayloadPush(Funds funds);
	
	Funds getFundsParmsPull();
	Funds getFundsPayloadPull();
	Funds getFundsParmsPush();
	Funds getFundsPayloadPush();
}
