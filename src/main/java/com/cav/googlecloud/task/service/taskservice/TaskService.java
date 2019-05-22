package com.cav.googlecloud.task.service.taskservice;

import com.cav.googlecloud.task.service.model.Funds;

public interface TaskService {

	void deleteFunds(Funds funds);
	void addFundsParams(Funds funds);
	void addFundsPayload(Funds funds);
	Funds getFundsParms();
	Funds getFundsPayload();
}
