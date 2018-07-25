package cn.wizzer.iot.mqtt.server.broker.handler;

import cn.wizzer.iot.mqtt.server.broker.packet.MqttPacket;
import cn.wizzer.iot.mqtt.server.tio.codec.MqttDecoder;
import cn.wizzer.iot.mqtt.server.tio.codec.MqttEncoder;
import cn.wizzer.iot.mqtt.server.tio.codec.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;

/**
 * MQTT消息处理
 * Created by wizzer on 2018
 */
public abstract class MqttAbsAioHandler implements AioHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(MqttAbsAioHandler.class);


    /**
     * 解码：把接收到的ByteBuffer，解码成应用可以识别的业务消息包
     */
    @Override
    public MqttPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
        if (readableLength < MqttPacket.HEADER_LENGHT) {
            return null;
        }
        //解析内容
        MqttMessage mqttMessage = MqttDecoder.decode(buffer);
        if (mqttMessage != null && "SUCCESS".equals(mqttMessage.decoderResult())) {
            MqttPacket mqttPacket = new MqttPacket();
            mqttPacket.setMqttMessage(mqttMessage);
            return mqttPacket;
        }
        return null;
    }

    /**
     * 编码：把业务消息包编码为可以发送的ByteBuffer
     * 消息头：MqttFixedHeader
     * 消息体：byte[]
     */
    @Override
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
        MqttPacket mqttPacket = (MqttPacket) packet;
        //写入消息体
        ByteBuffer buffer = MqttEncoder.doEncode(mqttPacket.getMqttMessage());
        buffer.flip();
        return buffer;
    }
}
