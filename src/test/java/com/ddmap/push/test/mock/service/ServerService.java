package com.ddmap.push.test.mock.service;

import com.ddmap.push.annotation.RemoteServiceContract;

@RemoteServiceContract
public interface ServerService {
	String callServer(String txt);
}
