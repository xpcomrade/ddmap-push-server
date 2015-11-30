package com.ddmap.push.test.mock.service;

import com.ddmap.push.annotation.RemoteServiceContract;

@RemoteServiceContract
public interface ClientService {
	String callClient(String txt);
}
