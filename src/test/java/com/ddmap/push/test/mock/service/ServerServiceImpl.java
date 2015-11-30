package com.ddmap.push.test.mock.service;

public class ServerServiceImpl implements ServerService {

	@Override
	public String callServer(String txt) {
		return "callServer " + txt;
	}

}
