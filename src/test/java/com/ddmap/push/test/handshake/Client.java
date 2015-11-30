package com.ddmap.push.test.handshake;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.ddmap.push.future.ChannelGroupFuture;
import com.ddmap.push.netty.handshake.ConnectorHandshakeProcessor;
import com.ddmap.push.netty.service.HsfConnector;
import com.ddmap.push.netty.service.HsfConnectorImpl;
import com.ddmap.push.pojo.HandshakeAck;
import com.ddmap.push.util.HsfOptions;

/**
 * @Title: Client.java
 * @Description: TODO(添加描述)
 * @date 2012-2-23 上午01:01:33
 * @version V1.0
 */
public class Client {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		HsfConnector connector = new HsfConnectorImpl();
		connector.setOption(HsfOptions.CHANNEL_NUM_PER_GROUP, 32);
		connector.setHandshakeProcessor(new ConnectorHandshakeProcessorImpl());
		ChannelGroupFuture groupFuture = connector.connect(new InetSocketAddress("192.168.1.26", 8082));

	}

	public static class ConnectorHandshakeProcessorImpl implements ConnectorHandshakeProcessor {

		@Override
		public Object getRequestAttachment() {
			return "handshake request";
		}

		@Override
		public Object getFinishAttachment() {
			return "handshake finish";
		}

		@Override
		public Map<String, Object> getInitAttributes() {
			return null;
		}

		@Override
		public void process(HandshakeAck handshakeAck) {
			System.out.println("process handshake ack " + handshakeAck);
		}

	}
}
