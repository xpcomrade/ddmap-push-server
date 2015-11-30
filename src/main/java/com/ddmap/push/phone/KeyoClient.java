package com.ddmap.push.phone;

import com.ddmap.push.compression.strategy.CompressionResult;
import com.ddmap.push.compression.strategy.ThresholdCompressionStrategy;
import com.ddmap.push.pojo.*;
import com.ddmap.push.serializer.KryoSerializer;
import com.ddmap.push.serializer.Serializer;
import com.ddmap.push.util.IntUtil;
import com.ddmap.push.util.UUIDUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * client端的类，通过NIO方式
 *
 * User: guo
 * Date: 13-10-15
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public class KeyoClient {
    public static final Serializer SERIALIZER = new KryoSerializer();
    private static ThresholdCompressionStrategy thresholdCompressionStrategy = new ThresholdCompressionStrategy();
    byte[] ip;
    int port;

    public KeyoClient(byte[] ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    static {
        SERIALIZER.register(HandshakeRequest.class);
        SERIALIZER.register(HandshakeAck.class);
        SERIALIZER.register(HandshakeFinish.class);
        SERIALIZER.register(PushRequest.class);
        SERIALIZER.register(PushAck.class);
        SERIALIZER.register(PushFinish.class);
        SERIALIZER.register(Heartbeat.class);
        SERIALIZER.register(OpenRequest.class);
        SERIALIZER.register(OpenAck.class);
    }

    private int byteSize = 512;
    private int times = 1;
    private ByteBuffer receivedBuffer = ByteBuffer.allocate(byteSize * times);
    private final String groupName = "3526680462445451";
//    private final String groupName = getDefaultGroupName();
    private Selector selector;
    private SocketChannel socketChannel = null;

    public void connect() throws Exception {
        socketChannel = SocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(ip), port);
        socketChannel.connect(isa);
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);

        new Thread(new NioWorker()).start();

        try {
            send(new HandshakeRequest(groupName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //需要改进
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    send(Heartbeat.getSingleton());
                } catch (IOException e) {
                    try {
                        Thread.sleep(1000);

                        socketChannel.close();
                        socketChannel = SocketChannel.open();
                        InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(ip), 8089);
                        socketChannel.connect(isa);
                        socketChannel.configureBlocking(false);

                        // 注册到selector
                        selector = Selector.open();
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        send(new HandshakeRequest(groupName));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        System.exit(1);
                    }
                }
            }
        }, 5L, 10L, TimeUnit.SECONDS);
    }

    //不需要动
    public void send(Object object) throws IOException {
        byte[] result = null;
        if (object instanceof Heartbeat) {
            result = new byte[4];
        } else {
            try {
                //序列化
                byte[] serializeMessage = SERIALIZER.serialize(object);
                //压缩
                CompressionResult compressionResult = thresholdCompressionStrategy.compress(serializeMessage);
                byte[] resBuffer = compressionResult.getBuffer();
                int length = resBuffer.length;
                byte[] bytes = new byte[length + 1];
                bytes[0] = compressionResult.isCompressed() ? (byte) 1 : (byte) 0;
                System.arraycopy(resBuffer, 0, bytes, 1, length);
                //编码
                if (bytes.length > 0) {
                    result = new byte[bytes.length + 4];
                    byte[] lenBytes = IntUtil.toBytes(bytes.length, ByteOrder.BIG_ENDIAN);
                    System.arraycopy(lenBytes, 0, result, 0, IntUtil.COUNT);
                    System.arraycopy(bytes, 0, result, 4, bytes.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            ByteBuffer sendBuffer = ByteBuffer.allocate(result.length);
            sendBuffer.put(result);
            sendBuffer.flip();
            if (socketChannel.isConnected()) {
                socketChannel.write(sendBuffer);
            }
        }
    }

    /**
     * 接收消息
     */
    private void receive(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // 读取消息
        try {
            socketChannel.read(receivedBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        receivedBuffer.flip();


        if (receivedBuffer.remaining() > 3) {
            receivedBuffer.mark();
            int length = receivedBuffer.getInt();

            if (length > receivedBuffer.capacity() - 4) {
                receivedBuffer.reset();
                synchronized (receivedBuffer) {
                    ByteBuffer temp = receivedBuffer;
                    receivedBuffer = ByteBuffer.allocate(length + 4);
                    receivedBuffer.put(temp);
                }
                return;
            } else if (receivedBuffer.remaining() < length) {
                receivedBuffer.reset();
                receivedBuffer.compact();
                return;
            }

            byte[] buffer = new byte[length];
            receivedBuffer.get(buffer);

            if (length == 1) {
                System.out.println("心跳消息");
            } else {
                try {
                    //解压缩
                    byte[] decompressbuffer = new byte[buffer.length - 1];
                    System.arraycopy(buffer, 1, decompressbuffer, 0, buffer.length - 1);
                    if (buffer[0] == 1) {
                        decompressbuffer = thresholdCompressionStrategy.decompress(decompressbuffer);
                    }
                    //反序列化
                    Object recMsg = SERIALIZER.deserialize(decompressbuffer);
                    if (recMsg instanceof HandshakeAck) {
                        // 构造握手完成消息包
                        HandshakeFinish finish = new HandshakeFinish(groupName);
                        // 发送
                        send(finish);
                    } else if (recMsg instanceof PushRequest) {
                        Set<String> set = new HashSet<String>();
                        //存储推送消息报到本地
                        HashMap<String, String> pushMap = ((PushRequest) recMsg).getMap();
                        for (Iterator<String> iterator = pushMap.keySet().iterator(); iterator.hasNext(); ) {
                            String mid = iterator.next();
                            String mv = pushMap.get(mid);
                            System.out.println("收到的推送消息为：" + mv);
                            set.add(mid);
                        }
                        //发送确认消息报
                        PushAck pushAck = new PushAck(groupName, set);
                        send(pushAck);
                    } else if (recMsg instanceof PushFinish) {
                        PushFinish pushFinish=(PushFinish)recMsg;
                        Set<String> set=pushFinish.getMessageIds();
                        //删除存储在本地的推送消息报文，表示服务器已经完成了计数
                        System.out.println("本地报文已经删除");
                        send(new OpenRequest(groupName,set));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        receivedBuffer.compact();
    }
    protected String getDefaultGroupName() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host = "UnknowHost";
        }
        return host + "@" + UUIDUtil.random();
    }

    /**
     * 通道读写任务
     */
    private class NioWorker implements Runnable {
        @Override
        public void run() {
            // 如果未准备好，则阻塞
            while (true) {
                try {
                    //有消息
                    if (selector.select() > 0) {
                        Set<SelectionKey> readyKeys = selector.selectedKeys();
                        Iterator<SelectionKey> it = readyKeys.iterator();
                        while (it.hasNext()) {
                            final SelectionKey key = it.next();
                            it.remove();
                            if (key.isReadable()) {
                                receive(key);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws UnknownHostException {
//        byte[] ip = InetAddress.getByName("172.16.11.11").getAddress();
        byte[] ip = InetAddress.getByName("192.168.9.217").getAddress();
//        byte[] ip = InetAddress.getByName("ddpush.ddmap.com").getAddress();
//        byte[] ip = InetAddress.getByName("61.152.236.207").getAddress();
//        byte[] ip = InetAddress.getByName("push.ddmap.com").getAddress();
        int port = 1883;
        for (int i = 0; i < 1; i++) {
            KeyoClient connector = new KeyoClient(ip, port);
            try {
                connector.connect();
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }
}
