package com.cav.googlecloud.task.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cav.googlecloud.task.service.model.Funds;
import com.cav.googlecloud.task.service.taskservice.TaskService;

@RestController
public class FundTaskController {
	
	@Autowired
	private TaskService taskservice;
	private static final Logger logger = LoggerFactory.getLogger(FundTaskController.class);
	
	@RequestMapping(value = "/AddFunds", method = RequestMethod.POST)
	public ResponseEntity<Funds>  addFunds(@RequestBody Funds funds) {
		logger.info("Add Funds to Queue "+funds.toString());
		taskservice.addFundsParams(funds);
		return new ResponseEntity<Funds>(funds, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/AddFundsPayload", method = RequestMethod.POST)
	public ResponseEntity<Funds>  addFundsPayload(@RequestBody Funds funds) {
		logger.info("Add Funds to Queue "+funds.toString());
		taskservice.addFundsPayload(funds);
		return new ResponseEntity<Funds>(funds, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/ListFundsParams", method = RequestMethod.POST)
	public ResponseEntity<Funds>  listFundsParams() {
		logger.info("Get Funds from Queue ");
		Funds funds = taskservice.getFundsParms();
		return new ResponseEntity<Funds>(funds, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/ListFundsPayload", method = RequestMethod.POST)
	public ResponseEntity<Funds>  listFundsPayload() {
		logger.info("Get Funds from Queue ");
		Funds funds = taskservice.getFundsPayload();
		return new ResponseEntity<Funds>(funds, HttpStatus.CREATED);
	}
}
