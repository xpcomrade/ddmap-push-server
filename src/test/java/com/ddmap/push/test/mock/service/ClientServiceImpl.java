package com.ddmap.push.test.mock.service;

public class ClientServiceImpl implements ClientService {

	@Override
	public String callClient(String txt) {
		return "callClient " + txt;
	}

}
