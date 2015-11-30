package com.ddmap.push.util;

import com.ddmap.push.exception.HsfTimeoutException;
import com.ddmap.push.future.ChannelGroupFuture;
import com.ddmap.push.netty.channel.HsfChannel;
import com.ddmap.push.pojo.HandshakeAck;
import com.ddmap.push.pojo.HandshakeFinish;
import com.ddmap.push.pojo.HandshakeRequest;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Title: HandshakeUtil.java
 * @Package com.ddmap.push.util
 * @Description: 握手处理辅助类
 * @author guo
 * @date 2012-2-24 上午12:18:40
 * @version V1.0
 */
public class HandshakeUtil {
	private static Logger logger = LoggerFactory.getLogger(HandshakeUtil.class);
	private static HashedWheelTimer timer = new HashedWheelTimer();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				timer.stop();
			}
		}));
	}

	public static void resetHandshakeTimeout(final HsfChannel channel) {
		Timeout timeout = channel.getHandshakeTimeout();
		if (timeout != null) {
			timeout.cancel();
		}

		//
		final Integer delay = LangUtil.parseInt(channel.getService().getOption(HsfOptions.HANDSHAKE_TIMEOUT), null);
		if (delay == null) {
			return;
		}
		TimerTask task = new TimerTask() {

			@Override
			public void run(Timeout timeout) throws Exception {
				logger.info("channel({}) handshake timeout({}ms), force to close it.", channel, delay);
				ChannelGroupFuture groupFuture = channel.getChannelGroupFuture();
				if (groupFuture != null) {
					groupFuture.addFailure(channel.getRemoteAddress(), new HsfTimeoutException("channel(" + channel
							+ ") handshake timeout(" + delay + "ms), force to close it."));
				}
				channel.close();
			}
		};
		timeout = timer.newTimeout(task, delay, TimeUnit.MILLISECONDS);
		channel.setHandshakeTimeout(timeout);
	}

	public static void cancelHandshakeTimeout(HsfChannel channel) {
		// cancel timeout
		Timeout timeout = channel.getHandshakeTimeout();
		if (timeout != null) {
			timeout.cancel();
		}
	}

	public static boolean isInitMsg(Object msg) {
		if (msg != null) {
			return (msg instanceof HandshakeRequest) || (msg instanceof HandshakeAck)
					|| (msg instanceof HandshakeFinish);
		}
		return false;
	}
}
