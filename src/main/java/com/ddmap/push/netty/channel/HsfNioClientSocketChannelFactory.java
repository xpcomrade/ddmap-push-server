package com.ddmap.push.netty.channel;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.ddmap.push.future.ChannelGroupFuture;
import com.ddmap.push.future.ChannelGroupFutureHolder;
import com.ddmap.push.util.HsfConstants;
import com.ddmap.push.util.TLSUtil;

/**
 * @author guo
 * @version V1.0
 */
public class HsfNioClientSocketChannelFactory extends NioClientSocketChannelFactory {

	public HsfNioClientSocketChannelFactory(Executor bossExecutor, Executor workerExecutor) {
		super(bossExecutor, workerExecutor);
	}

	public HsfNioClientSocketChannelFactory(Executor bossExecutor, Executor workerExecutor, int workerCount) {
		super(bossExecutor, workerExecutor, workerCount);
	}

	@Override
	public SocketChannel newChannel(ChannelPipeline pipeline) {
		ChannelGroupFuture channelFuture = (ChannelGroupFuture) TLSUtil.remove(HsfConstants.KEY_CURRENT_CHANNEL_FUTURE);
		SocketChannel channel = super.newChannel(pipeline);
		if (channelFuture != null) {
			ChannelGroupFutureHolder.put(channel.getId(), channelFuture);
		}

		return channel;
	}
}
