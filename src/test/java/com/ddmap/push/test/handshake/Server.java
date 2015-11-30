package com.ddmap.push.test.handshake;

import com.ddmap.push.netty.handshake.AcceptorHandshakeProcessor;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.pojo.HandshakeFinish;
import com.ddmap.push.pojo.HandshakeRequest;

import java.util.Map;


/**
 * @Title: Server.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午12:58:53
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.setHandshakeProcessor(new AcceptorHandshakeProcessorImpl());
		acceptor.bind(8082);
	}
	
	public static class AcceptorHandshakeProcessorImpl implements AcceptorHandshakeProcessor{

		@Override
		public Object getAckAttachment() {
			return "handshake ack";
		}

		@Override
		public Map<String, Object> getInitAttributes() {
			return null;
		}

		@Override
		public void process(HandshakeRequest handshakeRequest) {
			System.out.println("process handshake request " + handshakeRequest);
		}


        @Override
		public void process(HandshakeFinish handshakeFinish) {
			System.out.println("process handshake finish " + handshakeFinish);
		}
	}
}
