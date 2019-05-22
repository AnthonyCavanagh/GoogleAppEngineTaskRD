package com.cav.googlecloud.task.service.model;

import java.util.ArrayList;
import java.util.List;

public class Funds {

	public List<Fund>funds = new ArrayList<Fund>();

	public List<Fund> getFunds() {
		return funds;
	}

	@Override
	public String toString() {
		return "Funds [funds=" + funds + "]";
	}
}
