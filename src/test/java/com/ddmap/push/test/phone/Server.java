package com.ddmap.push.test.phone;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.netty.listener.EventBehavior;
import com.ddmap.push.netty.listener.MessageEventListener;
import com.ddmap.push.netty.service.HsfAcceptor;
import com.ddmap.push.netty.service.HsfAcceptorImpl;
import com.ddmap.push.serializer.Serializer;
import com.ddmap.push.util.HandshakeUtil;
import com.ddmap.push.util.HsfOptions;

/**
 * @Title: Server.java
 * @Package com.ddmap.push.test.phone
 * @date 2012-3-19 下午10:48:42
 * @version V1.0
 */
public class Server {
	public static void main(String[] args) {
		HsfAcceptor acceptor = new HsfAcceptorImpl();
		acceptor.getListeners().add(new ServerMsgListener());
		acceptor.setOption(HsfOptions.HANDSHAKE_TIMEOUT, 6000000);
		acceptor.setOption(HsfOptions.READ_IDLE_TIME, 6000000);
		// acceptor.setOption(HsfOptions.WRITE_IDLE_TIME, 6000000);
		// clear
		acceptor.getHandlers().clear();
		//
		Serializer serializer = new CustomSerializer();

//		acceptor.getHandlers().put("encoder", new CustomHsfEncoder(serializer));
//		acceptor.getHandlers().put("decode", new CustomHsfDecoder(serializer));

		acceptor.bind(8088);
	}

	private static class ServerMsgListener implements MessageEventListener {

		private PhoneService service = new PhoneServiceImpl();

		@Override
		public EventBehavior messageReceived(ChannelHandlerContext ctx, HsfChannel channel, MessageEvent e) {
			if (!HandshakeUtil.isInitMsg(e.getMessage())) {
				byte[] msg = (byte[]) e.getMessage();
				byte[] retValue = service.doExecute(msg);
				//
				channel.write(retValue);
			}

			return EventBehavior.Continue;
		}

	}
}
