package cn.m.util.cache.impl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.Tuple;
import cn.m.util.cache.ICacheManger;
import cn.m.util.cache.pool.ShardedJedisPool;
import cn.m.util.constants.CacheConstantEnum;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
/**
 * 缓存管理器redis实现. 对象压缩存储
 * 
 * @author mage
 * 
 * @param <T>
 */
public class JedisCacheManagerImpl<T extends Object> implements ICacheManger<T> {
	private final static Log LOGGER = LogFactory
			.getLog(JedisCacheManagerImpl.class);

	private ShardedJedisPool jedisPool;

	public ShardedJedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(ShardedJedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	private int getRealCacheTime(int seconds) {
		// 最长只能放30天
		seconds = seconds > CacheConstantEnum.MAX_CACHE_SEC.getCacheTime() ? CacheConstantEnum.MAX_CACHE_SEC.getCacheTime() : seconds;
		// 最短也要30分钟
		return seconds < CacheConstantEnum.MIN_CACHE_SEC.getCacheTime() ? CacheConstantEnum.MIN_CACHE_SEC.getCacheTime() : seconds;
	}
	public boolean setByKryo(String key, Object obj) {
		return setByKryo(key,obj,0);
	}

	public boolean setByKryo(String key, Object object_, int seconds) {
		if (object_ == null) {
			return false;
		}
		ShardedJedis commonJedis = null;
		byte[] data_ = null;
		boolean success = false;
		try {
			commonJedis = jedisPool.getResource();
			Kryo kryo = new Kryo();
			Output output = new Output(1, 4096); 
			kryo.writeObject(output, object_);
			data_ = output.toBytes();
			output.flush();
			output.close();
			commonJedis.setex(key.getBytes(), getRealCacheTime(seconds), data_);
			success = true;
		} catch (Exception e) {
			LOGGER.error("setByMsgPack object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return success;
	}

	public Object getByKryo(String key, Class<?> myClass) {
		return getByKryo(key,myClass,0);
	}

	public Object getByKryo(String key, Class<?> myClass, int seconds) {
		ShardedJedis commonJedis = null;
		Object object_ = null;
		try {
			commonJedis = jedisPool.getResource();
			byte[] data_ = commonJedis.get(key.getBytes());
			if (data_ == null) {
				return null;
			}
			Kryo kryo = new Kryo();
			Input input = new Input(data_);
			object_ = kryo.readObject(input, myClass);
			input.close();
			if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("getByMsgPack object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return object_;
	}

	/**
	 * 没有设置有效期的默认放置一天
	 */
	public boolean set(String key, T object_) {
		// 默认缓存24小时
		return set(key, object_, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	public boolean set(String key, T object_, int seconds) {
		if (object_ == null) {
			return false;
		}
		ShardedJedis commonJedis = null;
		byte[] data_ = null;
		boolean success = false;
		try {
			commonJedis = jedisPool.getResource();
			// 建立字节数组输出流
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			// 建立gzip压缩输出流
			GZIPOutputStream gzout = new GZIPOutputStream(o);
			// 建立对象序列化输出流
			ObjectOutputStream out = new ObjectOutputStream(gzout);
			out.writeObject(object_);
			out.flush();
			out.close();
			gzout.close();

			// 返回压缩字节流
			data_ = o.toByteArray();
			o.close();

			commonJedis.setex(key.getBytes(), getRealCacheTime(seconds), data_);
			success = true;
		} catch (Exception e) {
			LOGGER.error("set object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return success;
	}

	public T get(String key) {
		return get(key, 0);
	}
	@SuppressWarnings("unchecked")
	public T get(String key, int seconds) {
		ShardedJedis commonJedis = null;
		T object_ = null;
		try {
			commonJedis = jedisPool.getResource();
			byte[] data_ = commonJedis.get(key.getBytes());
			if (data_ == null) {
				return null;
			}
			// 建立字节数组输入流
			ByteArrayInputStream i = new ByteArrayInputStream(data_);
			// 建立gzip解压输入流
			GZIPInputStream gzin = new GZIPInputStream(i);
			// 建立对象序列化输入流
			ObjectInputStream in = new ObjectInputStream(gzin);
			// 按制定类型还原对象
			object_ = (T) in.readObject();

			i.close();
			gzin.close();
			in.close();
			if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("get object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return object_;
	}

	public void del(String key) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.del(key.getBytes());
		} catch (Exception e) {
			LOGGER.error("del cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public void setint(String key, int initialValue) {
		// 默认缓存24小时
		setint(key, initialValue, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	public void setint(String key, int initialValue, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.setex(key, getRealCacheTime(seconds),
					String.valueOf(initialValue));
		} catch (Exception e) {
			LOGGER.error("set int cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public int getint(String key) {
		ShardedJedis commonJedis = null;
		int number = 0;
		try {
			commonJedis = jedisPool.getResource();
			number = commonJedis.get(key) == null ? 0 :Integer.parseInt(commonJedis.get(key));
		} catch (Exception e) {
			LOGGER.error("get int cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return number;
	}

	public Long addint(String key, int increment, int seconds) {
		Long incredValue = 0L;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			incredValue = commonJedis.incrBy(key, increment);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("add int cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return incredValue;
	}
	
	public void reduceint(String key, int reduction, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.decrBy(key, reduction);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("reduce int cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public void setshort(String key, short initialValue) {
		// 默认缓存24小时
		setshort(key, initialValue, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	public void setshort(String key, short initialValue, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.setex(key, getRealCacheTime(seconds),
					String.valueOf(initialValue));
		} catch (Exception e) {
			LOGGER.error("set short cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public short getshort(String key) {
		ShardedJedis commonJedis = null;
		short number = 0;
		try {
			commonJedis = jedisPool.getResource();
			number = commonJedis.get(key) == null ? 0 : Short.parseShort(commonJedis.get(key));
		} catch (Exception e) {
			LOGGER.error("get short cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return number;
	}

	public void addshort(String key, short increment, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.incrBy(key, increment);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("add short cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public void reduceshort(String key, short reduction, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.decrBy(key, reduction);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("reduce short cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public void setlong(String key, long initialValue) {
		// 默认缓存24小时
		setlong(key, initialValue, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	public void setlong(String key, long initialValue, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.setex(key, getRealCacheTime(seconds),
					String.valueOf(initialValue));
		} catch (Exception e) {
			LOGGER.error("set long cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public long getlong(String key) {
		ShardedJedis commonJedis = null;
		long number = 0;
		try {
			commonJedis = jedisPool.getResource();
			number = commonJedis.get(key) == null ? 0 : Long.parseLong(commonJedis.get(key));
		} catch (Exception e) {
			LOGGER.error("get long cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return number;
	}

	public void addlong(String key, long increment, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.incrBy(key, increment);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("add long cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public void reducelong(String key, long reduction, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.decrBy(key, reduction);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("reduce long cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public long zadd(String key, long score, String member, int seconds) {
		long z = -1;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			z = commonJedis.zadd(key, score, member);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("zadd cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return z;
	}

	public long zaddByReport(String key, long score, String member) {
		long z = -1;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			z = commonJedis.zadd(key, score, member);
		} catch (Exception e) {
			LOGGER.error("zadd cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return z;
	}
	
	public long zadd(String key, long score, String member) {
		return zadd( key, score, member, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}
	
	public long zaddMap(String key, Map<String, Double> scoreMembers) {
		return zaddMap( key, scoreMembers, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}
	
	public long zaddMap(String key, Map<String, Double> scoreMembers, int seconds)
	{
		long z = -1;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			z = commonJedis.zadd(key, scoreMembers);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("zadd cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return z;
	}
	
	public Double zscore(String key, String member) {
		Double score = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			score = commonJedis.zscore(key, member);
		} catch (Exception e) {
			LOGGER.error("zscore cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return score;
	}

	@Override
	public List<Object> zaddPipeline(String keyPreffix, Collection<String> keySuffix,
			long score, String member) {
		return zaddPipeline(keyPreffix, keySuffix, score, member, 0);
	}

	@Override
	public List<Object> zaddPipeline(String keyPreffix, Collection<String> keySuffix,
			long score, String member, int seconds) {
		List<Object> result = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			ShardedJedisPipeline pipeline = commonJedis.pipelined();
			for(String suffix : keySuffix){
				String key = keyPreffix + suffix;
				pipeline.zadd(key, score, member);
				if(seconds > 0){
					pipeline.expire(key, seconds);
				}
			}
			result = pipeline.syncAndReturnAll();
		} catch (Exception e) {
			LOGGER.error("zscore cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return result;
	}

	public long zcard(String key) {
		long count = -1;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			count = commonJedis.zcard(key);
		} catch (Exception e) {
			LOGGER.error("zcard cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return count;
	}

	public long zremrangeByRank(String key, long start, long end) {
		long count = -1;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			count = commonJedis.zremrangeByRank(key, start, end);
		} catch (Exception e) {
			LOGGER.error("zremrangeByRank cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return count;
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
			double min, int offset, int count) {
		Set<Tuple> set = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			set = commonJedis.zrevrangeByScoreWithScores(key, max, min, offset,
					count);
		} catch (Exception e) {
			LOGGER.error("zrevrangeByScoreWithScores cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}
	
	public Set<Tuple> zrangeByScoreWithScores(String key, double min,
			double max, int offset, int count) {
		Set<Tuple> set = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			set = commonJedis.zrangeByScoreWithScores(key, min, max, offset,
					count);
		} catch (Exception e) {
			LOGGER.error("zrevrangeByScoreWithScores cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}

	
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Set<Tuple> set = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			set = commonJedis.zrevrangeByScoreWithScores(key, max, min);
		} catch (Exception e) {
			LOGGER.error("zrevrangeByScoreWithScores cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}
	
	public void zrem(String key, String members, int seconds ) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.zrem(key, members);
			commonJedis.expire( key, getRealCacheTime(seconds) );
		} catch (Exception e) {
			LOGGER.error("zrem cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	public void zrem(String key, String members) {
		zrem(key, members, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}
	
	@Override
	public void zrempipeline(String keyPreffix, Collection<String> keySuffixes,
			String... members) {
		this.zrempipeline(keyPreffix, keySuffixes, getDefaultCacheSec(), members);
	}

	@Override
	public void zrempipeline(String keyPreffix, Collection<String> keySuffixes, int seconds, String... members){
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			ShardedJedisPipeline pipeline =	commonJedis.pipelined();
			for(String suffix : keySuffixes){
				String cacheKey = keyPreffix + suffix;
				pipeline.zrem(cacheKey, members);
				if(seconds > 0){
					pipeline.expire(cacheKey, seconds);
				}
			}
			pipeline.sync();
		} catch (Exception e) {
			LOGGER.error("zrem cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	/**
	 * 缓存管理器hset初始化
	 */
	public void hset(String key, String field, String value) {
		hset(key, field, value, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	/**
	 * 缓存管理器hset初始化
	 */
	public void hset(String key, String field, String value, int seconds) {
		if (value == null) {
			return;
		}
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.hset(key, field, value);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("hset cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	/**
	 * 缓存管理器hgetAll获取hset数据总条数
	 * 
	 * @return
	 */
	public Map<String, String> hgetAll(String key) {
		Map<String, String> map = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			map = commonJedis.hgetAll(key);
		} catch (Exception e) {
			LOGGER.error("hgetAll cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return map;
	}

	public List<String> hvalues(String key)
	{
		List<String> list = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			list = commonJedis.hvals(key);
		} catch (Exception e) {
			LOGGER.error("hvalues cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<T> hvaluesObject(String key)
	{
		List<T> list = null;
		ShardedJedis commonJedis = null;
		T object = null;
		List<byte[]> listByte = null;
		try {
			list = new ArrayList<T>();
			commonJedis = jedisPool.getResource();
			listByte = (List<byte[]>)commonJedis.hvals(key.getBytes());			
			ByteArrayInputStream i = null;
			GZIPInputStream gzin = null;
			ObjectInputStream in = null;
	        for (byte[] valueItem : listByte) {
				// 建立字节数组输入流
				i = new ByteArrayInputStream(valueItem);
				// 建立gzip解压输入流
				gzin = new GZIPInputStream(i);
				// 建立对象序列化输入流
				in = new ObjectInputStream(gzin);
				// 按制定类型还原对象
				object = (T) in.readObject();
				list.add(object);
	        }
	        if(i != null && gzin != null && in != null)
	        {
				i.close();
				gzin.close();
				in.close();	        	
	        }
		} catch (Exception e) {
			LOGGER.error("hvaluesObject cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<T> hvaluesObjectNoZip(String key)
	{
		List<T> list = null;
		ShardedJedis commonJedis = null;
		T object = null;
		List<byte[]> listByte = null;
		try {
			list = new ArrayList<T>();
			commonJedis = jedisPool.getResource();
			listByte = (List<byte[]>)commonJedis.hvals(key.getBytes());			
			ByteArrayInputStream i = null;
			ObjectInputStream in = null;
	        for (byte[] valueItem : listByte) {
				// 建立字节数组输入流
				i = new ByteArrayInputStream(valueItem);
				// 建立对象序列化输入流
				in = new ObjectInputStream(i);
				// 按制定类型还原对象
				object = (T) in.readObject();
				list.add(object);
	        }
	        if(i != null && in != null)
	        {
				i.close();
				in.close();	        	
	        }
		} catch (Exception e) {
			LOGGER.error("hvaluesObject cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return list;
	}
	
	public List<String> hmget(String key, String... fields)
	{
		List<String> list = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			list = commonJedis.hmget(key, fields);
		} catch (Exception e) {
			LOGGER.error("hmget cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> hmgetObjects(String key, int seconds, String... fields)
	{
		if(key == null || fields == null)
		{
			return null;
		}	
		List<T> list = null;
		ShardedJedis commonJedis = null;
		byte[][] byteArray = new byte[fields.length][];
		for(int i=0; i<fields.length; i++)
		{
			byteArray[i] = fields[i].getBytes();
		}	
		List<byte[]> listByte = null;
		T object = null;
		try {
			commonJedis = jedisPool.getResource();
			listByte = commonJedis.hmget(key.getBytes(), byteArray);
			if(listByte == null || listByte.isEmpty())
			{
				return null;
			}	
			list = new ArrayList<T>();
			ByteArrayInputStream i = null;
			GZIPInputStream gzin = null;
			ObjectInputStream in = null;
	        for (byte[] byteItem : listByte) {
	        	if(byteItem == null || byteItem.length == 0){
	        		continue;
	        	}
				// 建立字节数组输入流
				i = new ByteArrayInputStream(byteItem);
				// 建立gzip解压输入流
				gzin = new GZIPInputStream(i);
				// 建立对象序列化输入流
				in = new ObjectInputStream(gzin);
				// 按制定类型还原对象
				object = (T) in.readObject();
				list.add(object);
	        }
	        if(i != null && gzin != null && in != null)
	        {
				i.close();
				gzin.close();
				in.close();	        	
	        }	
	        if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("hmgetObjects cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<T> hmgetObjectsNoZip(String key, String... fields){
		if(key == null || fields == null)
		{
			return null;
		}	
		List<T> list = null;
		ShardedJedis commonJedis = null;
		byte[][] byteArray = new byte[fields.length][];
		for(int i=0; i<fields.length; i++)
		{
			byteArray[i] = fields[i].getBytes();
		}	
		List<byte[]> listByte = null;
		T object = null;
		try {
			commonJedis = jedisPool.getResource();
			listByte = commonJedis.hmget(key.getBytes(), byteArray);
			if(listByte == null || listByte.isEmpty())
			{
				return null;
			}	
			list = new ArrayList<T>();
			ByteArrayInputStream i = null;
			ObjectInputStream in = null;
	        for (byte[] byteItem : listByte) {
	        	if(byteItem == null || byteItem.length == 0){
	        		continue;
	        	}
				// 建立字节数组输入流
				i = new ByteArrayInputStream(byteItem);
				// 建立对象序列化输入流
				in = new ObjectInputStream(i);
				// 按制定类型还原对象
				object = (T) in.readObject();
				list.add(object);
	        }
	        if(i != null && in != null)
	        {
				i.close();
				in.close();	        	
	        }	
		} catch (Exception e) {
			LOGGER.error("hmgetObjectsNoZip cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return list;		
	}
	
	public void hmset(String key, Map<String, String> map, int seconds)
	{
		if(map == null || map.isEmpty())
		{
			return;
		}	
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.hmset(key, map);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("hmset cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}
	
	/**
	 * 缓存管理器hincrBy自增hset某一field 默认有效期
	 */
	public void hincrBy(String key, String field, int value) {
		hincrBy(key, field, value, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	/**
	 * 缓存管理器hincrBy自增hset某一field 设置有效期
	 */
	public void hincrBy(String key, String field, int value, int seconds) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.hincrBy(key, field, value);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("hincrBy cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}

	/**
	 * 缓存管理器hdel删除hset某一field
	 */
	public void hdel(String key, String field, int seconds ) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.hdel(key, field);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("hdel cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}
	
	public void hdels(String key, int seconds, String... field)
	{
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.hdel(key, field);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("hdel cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}	
	}
	
	public void hdel(String key, String field ) {
		hdel(key, field, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime());
	}

	public void hdels(String key, String... field)
	{
		hdels(key, CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime(), field);
	}
	
	public long hlen(String key) {
		long count = -1;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			count = commonJedis.hlen(key);
		} catch (Exception e) {
			LOGGER.error("hlen cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return count;
	}

	public String hget(String key, String field) {
		String str = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			str = commonJedis.hget(key, field);
		} catch (Exception e) {
			LOGGER.error("hget cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return str;
	}
	
	public boolean hexists(String key, String field) {
		boolean str = false;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			str = commonJedis.hexists(key, field);
		} catch (Exception e) {
			LOGGER.error("hexists cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return str;
	}
	
	public boolean hexistsKeyField(String key, String field, int seconds) {
		boolean str = false;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			str = commonJedis.hexists(key, field);
			if(str){
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("hexists cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return str;
	}

	public long lpush(String key, String field, int seconds) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.lpush(key, field);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("lpush cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	

	public long lpush(String key, String[] fields, int seconds) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.lpush(key, fields);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("lpush fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	public long rpush(String key, String[] fields, int seconds) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.rpush(key, fields);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("rpush fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}		
	public long rpush(String key, String field, int seconds) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.rpush(key, field);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("rpush cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	public String lpop(String key, int seconds) {
		String str = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			str = commonJedis.lpop(key);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("lpop cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return str;
	}	
	
	public String rpop(String key, int seconds) {
		String str = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			str = commonJedis.rpop(key);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("rpop cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return str;
	}
	
	public long lrem(String key, int count, String filed, int seconds) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.lrem(key, count, filed);
			commonJedis.expire(key, getRealCacheTime( seconds ) );
		} catch (Exception e) {
			LOGGER.error("lrem cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}
	
	public long llen(String key) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			return commonJedis.llen(key);
		} catch (Exception e) {
			LOGGER.error("llen cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return 0;
	}
	
	public List<String> lrange(String key, long start, long end) {
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			return commonJedis.lrange(key, start, end);
		} catch (Exception e) {
			LOGGER.error("lrange cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return null;
	}	
	

	@Override
	public List<T> lrange(String key, long start, long end, int seconds) {
		List<T> list = new ArrayList<T>();
		List<byte[]> datas = null;
		ShardedJedis resource = null;
		try {
			resource = jedisPool.getResource();
			datas = resource.lrange(key.getBytes(), start, end);
			resource.expire(key, seconds);
		} catch (Exception e) {
			LOGGER.error("lrange error:" + e);
		}finally{
			jedisPool.closeResource(resource);
		}
		for(byte[] data : datas){
			list.add(getObjFromBytes(data));
		}
		return list;
	}

	public long sadd(String key, String field, int seconds ) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.sadd( key, field);
			commonJedis.expire( key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("sadd cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	public long sadd(String key, String[] fields, int seconds ) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.sadd( key, fields);
			commonJedis.expire( key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("sadd fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	public long srem(String key, String member, int seconds ) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.srem(key, member);
			commonJedis.expire( key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("srem fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	

	public String spop(String key, int seconds ) {
		String r = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.spop(key);
			commonJedis.expire( key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("spop fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	public long scard(String key) {
		long r = 0;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.scard(key);
		} catch (Exception e) {
			LOGGER.error("srem fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	public boolean sismember(String key, String member) {
		boolean r = false;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.sismember(key, member);
		} catch (Exception e) {
			LOGGER.error("sismember fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	

	public Set<String> smembers(String key) {
		Set<String> r = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.smembers( key );
		} catch (Exception e) {
			LOGGER.error("smembers fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}	
	
	/**
	 *设置有效期
	 */
	public void expire(String key, int seconds) {
		ShardedJedis commonJedis = null;
		try{
			commonJedis = jedisPool.getResource();
			commonJedis.expire(key, getRealCacheTime(seconds));
		}catch(Exception e){
			LOGGER.error("hincrBy cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}		
	}
	/**
	 *判断key是否存在
	 */
	public boolean exists(String key) {
		boolean c = false;
		ShardedJedis commonJedis = null;
		try{
			commonJedis = jedisPool.getResource();
			c = commonJedis.exists(key);
		}catch(Exception e){
			LOGGER.error("hincrBy cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}	
		return c;
	}
	
	public int getMaxCacheSec() {
		return CacheConstantEnum.MAX_CACHE_SEC.getCacheTime();
	}

	public int getMinCacheSec() {
		return CacheConstantEnum.MIN_CACHE_SEC.getCacheTime();
	}

	public int getDefaultCacheSec() {
		return CacheConstantEnum.DEFAULT_CACHE_SEC.getCacheTime();
	}
	
	public boolean hsetObject(String key, String field, T object, int seconds)
	{
		if (object == null) {
			return false;
		}
		ShardedJedis commonJedis = null;
		byte[] data_ = null;
		boolean success = false;
		try {
			commonJedis = jedisPool.getResource();
			// 建立字节数组输出流
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			// 建立gzip压缩输出流
			GZIPOutputStream gzout = new GZIPOutputStream(o);
			// 建立对象序列化输出流
			ObjectOutputStream out = new ObjectOutputStream(gzout);
			out.writeObject(object);
			out.flush();
			out.close();
			gzout.close();
			// 返回压缩字节流
			data_ = o.toByteArray();
			o.close();
			commonJedis.hset(key.getBytes(), field.getBytes(), data_);
			commonJedis.expire(key, getRealCacheTime(seconds));
			success = true;
		} catch (Exception e) {
			LOGGER.error("set object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return success;
	}

	public boolean hsetObjectNoGzip(String key, String field, T object, int seconds)
	{
		if (object == null) {
			return false;
		}
		ShardedJedis commonJedis = null;
		byte[] data_ = null;
		boolean success = false;
		try {
			commonJedis = jedisPool.getResource();
			// 建立字节数组输出流
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			// 建立对象序列化输出流
			ObjectOutputStream out = new ObjectOutputStream(o);
			out.writeObject(object);
			out.flush();
			out.close();
			// 返回压缩字节流
			data_ = o.toByteArray();
			o.close();
			commonJedis.hset(key.getBytes(), field.getBytes(), data_);
			commonJedis.expire(key, getRealCacheTime(seconds));
			success = true;
		} catch (Exception e) {
			LOGGER.error("hsetObjectNoGzip cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return success;
	}
	@SuppressWarnings("unchecked")
	public T hgetObject(String key, String field, int seconds)
	{
		ShardedJedis commonJedis = null;
		T object_ = null;
		try {
			commonJedis = jedisPool.getResource();
			byte[] data_ = commonJedis.hget(key.getBytes(), field.getBytes());
			if (data_ == null) {
				return null;
			}
			// 建立字节数组输入流
			ByteArrayInputStream i = new ByteArrayInputStream(data_);
			// 建立gzip解压输入流
			GZIPInputStream gzin = new GZIPInputStream(i);
			// 建立对象序列化输入流
			ObjectInputStream in = new ObjectInputStream(gzin);
			// 按制定类型还原对象
			object_ = (T) in.readObject();
			i.close();
			gzin.close();
			in.close();
			if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("get object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return object_;
	}
	@SuppressWarnings("unchecked")
	public T hgetObjectNoGzip(String key, String field, int seconds)
	{
		ShardedJedis commonJedis = null;
		T object_ = null;
		try {
			commonJedis = jedisPool.getResource();
			byte[] data_ = commonJedis.hget(key.getBytes(), field.getBytes());
			if (data_ == null) {
				return null;
			}
			// 建立字节数组输入流
			ByteArrayInputStream i = new ByteArrayInputStream(data_);
			// 建立对象序列化输入流
			ObjectInputStream in = new ObjectInputStream(i);
			// 按制定类型还原对象
			object_ = (T) in.readObject();
			i.close();
			in.close();
			if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("get object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return object_;
	}
	@SuppressWarnings("unchecked")
	public Map<String, T> hgetAllObject(String key, int seconds)
	{
		if(key == null)
		{
			return null;
		}	
		Map<byte[], byte[]> hMap = null;
		ShardedJedis commonJedis = null;
		Map<String, T> returnMap= null;
		T object = null;
		try {
			commonJedis = jedisPool.getResource();
			hMap = commonJedis.hgetAll(key.getBytes());
			if(hMap == null)
			{
				return null;
			}	
			returnMap = new HashMap<String, T>();
			Set<byte[]> keySet = hMap.keySet();
			ByteArrayInputStream i = null;
			GZIPInputStream gzin = null;
			ObjectInputStream in = null;
	        for (Iterator<byte[]> it = keySet.iterator(); it.hasNext();) {
	        	byte[] keyItem = it.next();
	        	byte[] valueItem = hMap.get(keyItem);
				// 建立字节数组输入流
				i = new ByteArrayInputStream(valueItem);
				// 建立gzip解压输入流
				gzin = new GZIPInputStream(i);
				// 建立对象序列化输入流
				in = new ObjectInputStream(gzin);
				// 按制定类型还原对象
				object = (T) in.readObject();
				returnMap.put(new String(keyItem), object);
	        }
	        if(i != null && gzin != null && in != null)
	        {
				i.close();
				gzin.close();
				in.close();	        	
	        }	
	        if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("hgetAll cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return returnMap;
	}
	
	public long lpush(byte[] key, byte[] strings, int seconds) {
		long id = 0;
		ShardedJedis commonJedis = null;
		try{
			commonJedis = jedisPool.getResource();
			id = commonJedis.lpush(key, strings);
			commonJedis.expire(key, getRealCacheTime(seconds));
		}catch(Exception e){
			LOGGER.error("hincrBy cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}
		return id;
	}
	
	@Override
	public long lpush(String key, T obj, int seconds) {
		long id = 0;
		ShardedJedis resource = null;
		try {
			resource = jedisPool.getResource();
			id = resource.lpush(key.getBytes(), getBytesFromObj(obj));
			if(seconds > 0){
				resource.expire(key, seconds);
			}
		} catch (Exception e) {
			LOGGER.error("lpush obj error: " + e);
		}finally{
			jedisPool.closeResource(resource);
		}
		return id;
	}

	public byte[] rpop(byte[] key, int seconds) {
		byte[] obj = null;
		ShardedJedis commonJedis = null;
		try{
			commonJedis = jedisPool.getResource();
			obj = commonJedis.rpop(key);
			commonJedis.expire(key, getRealCacheTime(seconds));
		}catch(Exception e){
			LOGGER.error("hincrBy cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}
		return obj;
	}

	public Set<String> zrevrange(String key, long start, long end) {
		return zrevrange(key, start, end, 0);
	}

	public Set<String> zrevrange(String key, long start, long end, int seconds) {
		Set<String> set = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			set = commonJedis.zrevrange(key, start, end);
			if(seconds != 0)
			{
				commonJedis.expire(key, getRealCacheTime(seconds));				
			}	
		} catch (Exception e) {
			LOGGER.error("zrevrange cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}
	
	public Set<String> zrange(String key, long start, long end) {
		return zrange(key, start, end, 0);
	}

	public Set<String> zrange(String key, long start, long end, int seconds)
	{
		Set<String> set = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			set = commonJedis.zrange( key, start, end );
			if(seconds != 0)
			{
				commonJedis.expire(key, getRealCacheTime(seconds));				
			}	
		} catch (Exception e) {
			LOGGER.error("zrange cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}
	
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		return zrangeWithScores(key, start, end, 0);
	}

	public Set<Tuple> zrangeWithScores(String key, long start, long end, int seconds)
	{
		Set<Tuple> set = null;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			set = commonJedis.zrangeWithScores(key, start, end);
			if(seconds != 0)
			{
				commonJedis.expire(key, getRealCacheTime(seconds));				
			}	
		} catch (Exception e) {
			LOGGER.error("zrange cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}

	public long zcount(String key, long min, long max)
	{
		ShardedJedis commonJedis = null;
		long length = 0;
		try{
			commonJedis = jedisPool.getResource();
			length = commonJedis.zcount(key, min, max);
		}catch(Exception e){
			LOGGER.error("zcount cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}
		return length;
	}
	
	public void zremrangeByScore(String key, long start, long end)
	{
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			LOGGER.error("del cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}
	public void zincrby(String key, String member, long score, int seconds){
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			commonJedis.zincrby(key, score, member);
			if(seconds != 0){
				commonJedis.expire(key, getRealCacheTime(seconds));				
			}
		} catch (Exception e) {
			LOGGER.error("del cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
	}
	public long zrevrank(String key, String member){
		ShardedJedis commonJedis = null;
		long index = -1;
		try {
			commonJedis = jedisPool.getResource();
			Long indexL = commonJedis.zrevrank(key, member);
			if(indexL != null){
				index = indexL.longValue();
			}
		} catch (Exception e) {
			LOGGER.error("del cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return index;
	}
	public int ttl(String key) {
		ShardedJedis commonJedis = null;
		int number = 0;
		try {
			commonJedis = jedisPool.getResource();
			number = commonJedis.get(key) == null ? 0 : Integer.parseInt(commonJedis.ttl(key)+"");
		} catch (Exception e) {
			LOGGER.error("get ttl cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return number;
	}
	
	/**
	 * 反垃圾用户有效期设置(时间可变的)
	 *设置有效期
	 */
	public void expireSpamUser(String key, int seconds) {
		ShardedJedis commonJedis = null;
		try{
			commonJedis = jedisPool.getResource();
			commonJedis.expire(key, seconds);
		}catch(Exception e){
			LOGGER.error("hincrBy cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}		
	}
	
	/**
	 * 判断member是否存在zset中
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean zismember(String key, String member) {
		boolean r = false;
		ShardedJedis commonJedis = null;
		try {
			commonJedis = jedisPool.getResource();
			r = commonJedis.zrank(key, member) != null ? true : false;
		} catch (Exception e) {
			LOGGER.error("sismember fields cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return r;
	}
	
	public long hkeys(String key)
	{
		ShardedJedis commonJedis = null;
		long length = 0;
		try{
			commonJedis = jedisPool.getResource();
			length = commonJedis.hlen(key);
		}catch(Exception e){
			LOGGER.error("hincrBy cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}
		return length;
	}

	public Set<String> hGetKeys(String key)
	{
		ShardedJedis commonJedis = null;
		Set<String> set = null;
		try{
			commonJedis = jedisPool.getResource();
			set = commonJedis.hkeys(key);
		}catch(Exception e){
			LOGGER.error("hGetKeys cache error:",e);
		}finally {
			jedisPool.closeResource(commonJedis);
		}
		return set;
	}
	
	public boolean hsetObjectList(String key, Map<String, T> map, int seconds) {
		if(map == null || map.size() == 0)
		{
			return false;
		}	
		ShardedJedis commonJedis = null;
		byte data_[] = null;
		try {
			commonJedis = jedisPool.getResource();
			Set<String> mapKeys = map.keySet();
			Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>();
			for(String mapKey : mapKeys)
			{
				// 建立字节数组输出流
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				T object = map.get(mapKey);
				// 建立gzip压缩输出流
				GZIPOutputStream gzout = new GZIPOutputStream(o);
				// 建立对象序列化输出流
				ObjectOutputStream out = new ObjectOutputStream(gzout);
				out.writeObject(object);
				out.flush();
				out.close();
				gzout.close();
				// 返回压缩字节流
				data_ = o.toByteArray();
				o.close();
				byteMap.put(mapKey.getBytes(), data_);
			}	
			commonJedis.hmset(key.getBytes(), byteMap);
			if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("hsetObjectList cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return true;
	}
	
	public boolean hsetObjectListNoGzip(String key, Map<String, T> map, int seconds) {
		if(map == null || map.size() == 0)
		{
			return false;
		}	
		ShardedJedis commonJedis = null;
		byte data_[] = null;
		try {
			commonJedis = jedisPool.getResource();
			Set<String> mapKeys = map.keySet();
			Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>();
			for(String mapKey : mapKeys)
			{
				// 建立字节数组输出流
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				T object = map.get(mapKey);
				// 建立对象序列化输出流
				ObjectOutputStream out = new ObjectOutputStream(o);
				out.writeObject(object);
				out.flush();
				out.close();
				// 返回压缩字节流
				data_ = o.toByteArray();
				o.close();
				byteMap.put(mapKey.getBytes(), data_);
			}	
			commonJedis.hmset(key.getBytes(), byteMap);
			if (seconds > 0) {
				commonJedis.expire(key, getRealCacheTime(seconds));
			}
		} catch (Exception e) {
			LOGGER.error("hsetObjectListNoGzip cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return true;
	}
	public long ttl_rest(String key) {
		ShardedJedis commonJedis = null;
		long len = 0;
		try {
			commonJedis = jedisPool.getResource();
			len = commonJedis.ttl(key);
		} catch (Exception e) {
			LOGGER.error("get ttl cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return len;
	}
	
	@Override
	public byte[] getBytesFromObj(T obj){
		byte[] data = null;
		try {
			// 建立字节数组输出流
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			// 建立gzip压缩输出流
			GZIPOutputStream gzout = new GZIPOutputStream(o);
			// 建立对象序列化输出流
			ObjectOutputStream out = new ObjectOutputStream(gzout);
			out.writeObject(obj);
			out.flush();
			out.close();
			// 返回压缩字节流
			data = o.toByteArray();
			o.close();
		} catch (Exception e) {
			LOGGER.error("getBytesFromObj error:" + e);
		}
		return data;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getObjFromBytes(byte[] data){
		T obj = null;
		try {
			// 建立字节数组输入流
			ByteArrayInputStream i = new ByteArrayInputStream(data);
			// 建立gzip解压输入流
			GZIPInputStream gzin = new GZIPInputStream(i);
			// 建立对象序列化输入流
			ObjectInputStream in = new ObjectInputStream(gzin);
			// 按制定类型还原对象
			obj = (T) in.readObject();
			i.close();
			in.close();
		} catch (Exception e) {
			LOGGER.error("getObjFromBytes error: " + e);
		}
		return obj;
	}

	@Override
	public long zrank(String key, String member, int seconds) {
		ShardedJedis commonJedis = null;
		long len = 0;
		try {
			commonJedis = jedisPool.getResource();
			len = commonJedis.zrank(key, member);
		} catch (Exception e) {
			LOGGER.error("get zrank cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return len;
	}

	@Override
	public long zaddObject(String key, long score, T object, int seconds) {
		if (object == null) {
			return -1;
		}
		
		long z = -1;
		ShardedJedis commonJedis = null;
		byte[] data_ = null;
		try {
			commonJedis = jedisPool.getResource();
			// 建立字节数组输出流
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			// 建立gzip压缩输出流
			GZIPOutputStream gzout = new GZIPOutputStream(o);
			// 建立对象序列化输出流
			ObjectOutputStream out = new ObjectOutputStream(gzout);
			out.writeObject(object);
			out.flush();
			out.close();
			gzout.close();
			// 返回压缩字节流
			data_ = o.toByteArray();
			o.close();
			z = commonJedis.zadd(key.getBytes(), score, data_);
			commonJedis.expire(key, getRealCacheTime(seconds));
		} catch (Exception e) {
			LOGGER.error("zadd Object cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return z;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<T> zrangeObject(String key, long start, long end, int seconds) {
		if(key == null)
		{
			return null;
		}
		Set<byte[]> zSet = null;
		ShardedJedis commonJedis = null;
		Set<T> returnSet= null;
		T object = null;
		try {
			commonJedis = jedisPool.getResource();
			zSet = commonJedis.zrange( key.getBytes(), start, end );
			if(zSet == null)
			{
				return null;
			}
			returnSet = new HashSet<T>();
			ByteArrayInputStream i = null;
			GZIPInputStream gzin = null;
			ObjectInputStream in = null;
			for (Iterator<byte[]> it = zSet.iterator(); it.hasNext();) {
	        	byte[] valueItem = it.next();
				// 建立字节数组输入流
				i = new ByteArrayInputStream(valueItem);
				// 建立gzip解压输入流
				gzin = new GZIPInputStream(i);
				// 建立对象序列化输入流
				in = new ObjectInputStream(gzin);
				// 按制定类型还原对象
				object = (T) in.readObject();
				returnSet.add(object);
	        }
			if(i != null && gzin != null && in != null)
	        {
				i.close();
				gzin.close();
				in.close();	        	
	        }	
			if(seconds != 0)
			{
				commonJedis.expire(key, getRealCacheTime(seconds));				
			}	
		} catch (Exception e) {
			LOGGER.error("zrange cache error:", e);
		} finally {
			jedisPool.closeResource(commonJedis);
		}
		return returnSet;
	}
	
	@Override
	public List<Object> pipelineHgetall(String prefixKey, Collection<String> suffixKeys){
		List<Object> list = null;
		ShardedJedis resource = null;
		try {
			resource = jedisPool.getResource();
			ShardedJedisPipeline pipe = resource.pipelined();
			for(String suffix : suffixKeys){
				pipe.hgetAll((prefixKey + suffix).getBytes());
			}
			list = pipe.syncAndReturnAll();
		} catch (Exception e) {
			LOGGER.error("pipelineHge wrong!", e);
		} finally {
			jedisPool.closeResource(resource);
		}
		return list;
	}
	
	public void pipelineHsetall(String prefix, Map<String, Map<byte[], byte[]>> values){
		ShardedJedis resource = null;
		try {
			resource = jedisPool.getResource();
			ShardedJedisPipeline pipe = resource.pipelined();
			for(String key : values.keySet()){
				byte[] byteKey = (prefix + key).getBytes();
				pipe.hmset(byteKey, values.get(key));
			}
			pipe.sync();
		} catch (Exception e) {
			LOGGER.error("pipelineHsetall error!\n", e);
		}finally{
			jedisPool.closeResource(resource);
		}
	}
	
	@Override
	public List<Object> pipelineExist(String prefixKey, Collection<String> suffixKeys){
		ShardedJedis resource = null;
		List<Object> result = null;
		try {
			resource = jedisPool.getResource();
			ShardedJedisPipeline pipe = resource.pipelined();
			for(String suffix : suffixKeys){
				pipe.exists(prefixKey + suffix);
			}
			result = pipe.syncAndReturnAll();
		} catch (Exception e) {
			LOGGER.error("pipelineExist error!", e);
		} finally {
			jedisPool.closeResource(resource);
		}
		return result;
	}
}
