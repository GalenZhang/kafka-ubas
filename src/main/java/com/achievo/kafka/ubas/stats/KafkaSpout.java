package com.achievo.kafka.ubas.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.achievo.kafka.ubas.conf.KafkaConfigureAPI.KafkaParam;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaSpout implements IRichSpout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8032195239737611939L;

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSpout.class);

	private SpoutOutputCollector collector;

	private ConsumerConnector consumer;

	private String topic;

	private static ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", KafkaParam.ZK_HOSTS);
		props.put("group.id", KafkaParam.GROUP_ID);
		props.put("zookeeper.session.timeout.ms", "40000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	public KafkaSpout(String topic) {
		this.topic = topic;
	}

	public void ack(Object arg0) {
	}

	public void activate() {
		try {
			this.consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
			Map<String, Integer> topicMap = new HashMap<String, Integer>();
			topicMap.put(topic, new Integer(1));
			Map<String, List<KafkaStream<byte[], byte[]>>> streamMap = consumer.createMessageStreams(topicMap);
			KafkaStream<byte[], byte[]> stream = streamMap.get(topic).get(0);
			ConsumerIterator<byte[], byte[]> iter = stream.iterator();
			while (iter.hasNext()) {
				String value = new String(iter.next().message());
				LOGGER.info("[ Consumer ] Message is : " + value);
				collector.emit(new Values(value), value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Spout has error,msg is " + e.getMessage());
		}
	}

	public void close() {

	}

	public void deactivate() {

	}

	public void fail(Object arg0) {

	}

	public void nextTuple() {

	}

	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("KafkaSpout"));
	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
