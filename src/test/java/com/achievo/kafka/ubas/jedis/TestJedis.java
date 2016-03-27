package com.achievo.kafka.ubas.jedis;

import com.achievo.kafka.ubas.util.JedisFactory;

import redis.clients.jedis.Jedis;

public class TestJedis {
	public static void main(String[] args) {
		Jedis jedis = JedisFactory.getJedisInstance("real-time");
//		jedis.set("20150805" + "_" + "jedis", "1");
		jedis.flushAll();
	}
}
