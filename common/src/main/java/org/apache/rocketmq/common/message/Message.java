/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息封装类：包括消息的基础属性和扩展属性和消息体
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 8445773977080406428L;

    private String topic;
    private int flag;
    private Map<String, String> properties;
    private byte[] body;
    private String transactionId;

    public Message() {
    }

    public Message(String topic, byte[] body) {
        this(topic, "", "", 0, body, true);
    }

    /**
     *
     * @param topic
     * @param tags 消息扩展属性：消息TAG，用于消息过滤
     * @param keys Message索引值，多个用空格隔开，RocketMQ可以根据这些keys快速检索到索引文件
     * @param flag 消息flag，不作处理
     * @param body 消息实体
     * @param waitStoreMsgOK 用于表示消息发送之后是否需要等待消息存储之后返回响应
     */
    public Message(String topic, String tags, String keys, int flag, byte[] body, boolean waitStoreMsgOK) {
        this.topic = topic;
        this.flag = flag;
        this.body = body;

        if (tags != null && tags.length() > 0)
            this.setTags(tags);

        if (keys != null && keys.length() > 0)
            this.setKeys(keys);

        this.setWaitStoreMsgOK(waitStoreMsgOK);
    }

    public Message(String topic, String tags, byte[] body) {
        this(topic, tags, "", 0, body, true);
    }

    public Message(String topic, String tags, String keys, byte[] body) {
        this(topic, tags, keys, 0, body, true);
    }

    public void setKeys(String keys) {
        this.putProperty(MessageConst.PROPERTY_KEYS, keys);
    }

    void putProperty(final String name, final String value) {
        if (null == this.properties) {
            this.properties = new HashMap<String, String>();
        }

        this.properties.put(name, value);
    }

    void clearProperty(final String name) {
        if (null != this.properties) {
            this.properties.remove(name);
        }
    }

    public void putUserProperty(final String name, final String value) {
        if (MessageConst.STRING_HASH_SET.contains(name)) {
            throw new RuntimeException(String.format(
                "The Property<%s> is used by system, input another please", name));
        }

        if (value == null || value.trim().isEmpty()
            || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "The name or value of property can not be null or blank string!"
            );
        }

        this.putProperty(name, value);
    }

    public String getUserProperty(final String name) {
        return this.getProperty(name);
    }

    public String getProperty(final String name) {
        if (null == this.properties) {
            this.properties = new HashMap<String, String>();
        }

        return this.properties.get(name);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return this.getProperty(MessageConst.PROPERTY_TAGS);
    }

    public void setTags(String tags) {
        this.putProperty(MessageConst.PROPERTY_TAGS, tags);
    }

    public String getKeys() {
        return this.getProperty(MessageConst.PROPERTY_KEYS);
    }

    public void setKeys(Collection<String> keys) {
        StringBuffer sb = new StringBuffer();
        for (String k : keys) {
            sb.append(k);
            sb.append(MessageConst.KEY_SEPARATOR);
        }

        this.setKeys(sb.toString().trim());
    }

    /**
     * delayTimeLevel：消息延迟级别，用于消息重试或者是定时消息，只有固定几个消息延迟级别
     * @return
     */
    public int getDelayTimeLevel() {
        String t = this.getProperty(MessageConst.PROPERTY_DELAY_TIME_LEVEL);
        if (t != null) {
            return Integer.parseInt(t);
        }

        return 0;
    }

    public void setDelayTimeLevel(int level) {
        this.putProperty(MessageConst.PROPERTY_DELAY_TIME_LEVEL, String.valueOf(level));
    }

    public boolean isWaitStoreMsgOK() {
        String result = this.getProperty(MessageConst.PROPERTY_WAIT_STORE_MSG_OK);
        if (null == result)
            return true;

        return Boolean.parseBoolean(result);
    }

    public void setWaitStoreMsgOK(boolean waitStoreMsgOK) {
        this.putProperty(MessageConst.PROPERTY_WAIT_STORE_MSG_OK, Boolean.toString(waitStoreMsgOK));
    }

    public void setInstanceId(String instanceId) {
        this.putProperty(MessageConst.PROPERTY_INSTANCE_ID, instanceId);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getBuyerId() {
        return getProperty(MessageConst.PROPERTY_BUYER_ID);
    }

    public void setBuyerId(String buyerId) {
        putProperty(MessageConst.PROPERTY_BUYER_ID, buyerId);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Message{" +
            "topic='" + topic + '\'' +
            ", flag=" + flag +
            ", properties=" + properties +
            ", body=" + Arrays.toString(body) +
            ", transactionId='" + transactionId + '\'' +
            '}';
    }
}
