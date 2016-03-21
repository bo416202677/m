package cn.m.util.cache.pool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;

public class ShardedJedisPool {
	
	private Logger LOGGER = Logger.getLogger(ShardedJedisPool.class);
	
	private redis.clients.jedis.ShardedJedisPool shardedJedisPool;
	
	// 注入格式(name1:host1:port1,name2:host2:port2,name3:host3:port3)
	public ShardedJedisPool(final GenericObjectPoolConfig poolConfig,
			String configStr, int timeout) {
		try {
			List<JedisShardInfo> infoList = new ArrayList<JedisShardInfo>();
			String[] configArr = configStr.split(",|，");
			for (String config : configArr) {
				String[] infoParams = config.split(":|：");
				if (infoParams.length != 4) {
					LOGGER.error("redis配置格式非法:" + config);
					System.out.println("--------------"+infoParams[2]+"--------------"+infoParams[1]+"--------------"+infoParams[0]);
					JedisShardInfo shareInfo = new JedisShardInfo(infoParams[1],
							Integer.parseInt(infoParams[2]), timeout, infoParams[0]);
					infoList.add(shareInfo);
				}else{
					System.out.println(infoParams[3]+"--------------"+infoParams[2]+"--------------"+infoParams[1]+"--------------"+infoParams[0]);
					JedisShardInfo shareInfo = new JedisShardInfo(infoParams[1],
							Integer.parseInt(infoParams[2]), timeout, infoParams[0]);
					shareInfo.setPassword(infoParams[3].replace("+", ":"));
					infoList.add(shareInfo);
				}
			}
			LOGGER.debug("load redis pool size:" + infoList.size());
			shardedJedisPool = new redis.clients.jedis.ShardedJedisPool(poolConfig, infoList);
		} catch (Exception e) {
			LOGGER.error("redis配置格式非法:", e);
			throw new RuntimeException("redis配置格式非法:", e);
		}
	}

	public ShardedJedis getResource() {
		if (this.shardedJedisPool == null) {
			return null;
		}
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = this.shardedJedisPool.getResource();
		} catch (Exception e) {
			LOGGER.debug("get jedisWrap from shardedJedisPool error", e);
		}
		return shardedJedis;
	}

	public void closeResource(final ShardedJedis resource) {
		if (resource != null) {
			resource.close();
		}
	}

}