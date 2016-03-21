package cn.m.util.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Tuple;

public interface ICacheManger<T extends Object> {
	
	/**
	 * 获取有效期时间
	 * 
	 * @return
	 */
	public int getMaxCacheSec();

	public int getMinCacheSec();

	public int getDefaultCacheSec();
	
	/**
	 * messagepack进行组包解包
	public boolean setByMsgPack(String key,Object obj);
	public boolean setByMsgPack(String key,Object obj, int seconds);
	
	public Object getByMsgPack(String key,Class<?> myClass);
	public Object getByMsgPack(String key,Class<?> myClass, int seconds);
	*/
	
	/**
	 * kryo进行组包解包
	 */
	public boolean setByKryo(String key,Object obj);
	public boolean setByKryo(String key,Object obj, int seconds);
	
	public Object getByKryo(String key,Class<?> myClass);
	public Object getByKryo(String key,Class<?> myClass, int seconds);

	/**
	 * 缓存管理器set
	 * 
	 * @param key
	 * @param ob
	 */
	public boolean set(String key, T ob);

	/**
	 * 缓存管理器set
	 * 
	 * @param key
	 * @param ob
	 * @param seconds	有效期
	 * @return
	 */
	public boolean set(String key, T ob, int seconds);

	/**
	 * 缓存管理器获取
	 * 
	 * @param key
	 * @return
	 */
	public T get(String key);
	/**
	 * 缓存管理器获取
	 * 
	 * @param key
	 * @param seconds	有效期
	 * @return
	 */
	public T get(String key, int seconds);

	/**
	 * 缓存管理器删除
	 */
	public void del(String key);

	/**
	 * 计数器型原子操作,int
	 */
	public void setint(String key, int initialValue);

	public void setint(String key, int initialValue, int seconds);

	public int getint(String key);

	public Long addint(String key, int increment, int seconds);

	public void reduceint(String key, int reduction, int seconds);

	/**
	 * 计数器型原子操作,short
	 */
	public void setshort(String key, short initialValue);

	public void setshort(String key, short initialValue, int seconds);

	public short getshort(String key);

	public void addshort(String key, short increment, int seconds);

	public void reduceshort(String key, short reduction, int seconds);

	/**
	 * 计数器型原子操作,long
	 */
	public void setlong(String key, long initialValue);

	public void setlong(String key, long initialValue, int seconds);

	public long getlong(String key);

	public void addlong(String key, long increment, int seconds);

	public void reducelong(String key, long reduction, int seconds);

	/**
	 * 有序set操作, zset
	 * 
	 * @param key		键名
	 * @param score		排序值
	 * @param member	元素内容
	 * @param seconds	有效期
	 * @return
	 */
	//增加元素
	public long zadd(String key, long score, String member, int seconds);
	public long zadd(String key, long score, String member);
	//批量添加zset元素
	public long zaddMap(String key, Map<String, Double> scoreMembers);
	public long zaddMap(String key, Map<String, Double> scoreMembers, int seconds);
	List<Object> zaddPipeline(String keyPreffix, Collection<String> keySuffix, long score, String member);
	List<Object> zaddPipeline(String keyPreffix, Collection<String> keySuffix, long score, String member, int seconds);
	/**
	 * 只有用户举报功能才能使用这个方法, 其他地方不需要这个方法
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */	
	public long zaddByReport(String key, long score, String member);
	//获取元素的排序值
	public Double zscore(String key, String member);
	//获取列表的元素总数
	public long zcard(String key);
	//删除排序值从小到大排序结果中从start开始到end结束的元素
	public long zremrangeByRank(String key, long start, long end);
	//获取排序值从大到小排序并在给定区间（min~max）的结果中从offset开始,取count个元素
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
			double min, int offset, int count);
	//获取排序值从小到大排序并在给定区间（min~max）的结果中从offset开始,取count个元素
	public Set<Tuple> zrangeByScoreWithScores(String key, double min,
			double max, int offset, int count);

	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);
	//删除列表中的元素
	public void zrem(String key, String members);
	public void zrem(String key, String members, int second );
	// 通过管道的方式从zset中移除元素
	void zrempipeline(String keyPreffix, Collection<String> keySuffixes, String... members);
	void zrempipeline(String keyPreffix, Collection<String> keySuffixes, int seconds, String... members);
	public Set<String> zrevrange(String key, long start, long end);
	public Set<String> zrevrange(String key, long start, long end, int seconds);
	public Set<String> zrange(String key, long start, long end);
	public Set<String> zrange(String key, long start, long end, int seconds);
	public Set<Tuple> zrangeWithScores(String key, long start, long end);
	public Set<Tuple> zrangeWithScores(String key, long start, long end, int seconds);
	
	/**
	 * 获取zset中指定分值之间的元素个数
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public long zcount(String key, long min, long max);
	
	/**
	 * 根据权重删除zset元素
	 * @param key
	 * @param start
	 * @param end
	 */
	public void zremrangeByScore(String key, long start, long end);
	
	/**
	 * 增加元素的score值，如果元素不存在，则创建元素
	 * @param key
	 * @param member
	 * @param score
	 * @param seconds
	 */
	public void zincrby(String key, String member, long score, int seconds);
	
	/**
	 * 获取元素的排名index
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrevrank(String key, String member);
	
	/**
	 * 判断member是否存在zset中
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean zismember(String key, String member);
	
	
	/**
	 * map操作集合
	 * @param key		键名
	 * @param field		元素名称
	 * @param value		元素值
	 */
	public void hset(String key, String field, String value);
	
	public void hset(String key, String field, String value, int seconds);

	public Map<String, String> hgetAll(String key);

	public List<String> hvalues(String key);
	
	public List<T> hvaluesObject(String key);
	
	public List<T> hvaluesObjectNoZip(String key);

	public List<String> hmget(String key, String... fields);
	
	public List<T> hmgetObjects(String key, int seconds, String... fields);
	
	public List<T> hmgetObjectsNoZip(String key, String... fields);
	
	public void hmset(String key, Map<String, String> map, int seconds);
	
	public void hincrBy(String key, String field, int value);
	
	public void hincrBy(String key, String field, int value, int seconds);

	public void hdel(String key, String field);
	public void hdels(String key, String... field);
	public void hdel(String key, String field, int second);
	public void hdels(String key, int seconds, String... field);
	public long hlen(String key);
	
	public String hget(String key, String field);
	
	public boolean hexists(String key, String field);
	
	public boolean hexistsKeyField(String key, String field, int seconds);
	
	/**
	 * list 的操作
	 * @param key
	 * @param field
	 * @param seconds
	 * @return
	 */
	public long lpush(String key, String field, int seconds);
	public long rpush(String key, String field, int seconds);
	public String lpop(String key, int seconds);	
	public String rpop(String key, int seconds);
	public long lrem(String key, int count, String filed, int seconds);
	public long llen(String key);
	public List<String> lrange(String key, long start, long end);
	public List<T> lrange(String key, long start, long end, int seconds);
	public long lpush(String key, String[] fields, int seconds);
	public long rpush(String key, String[] fields, int seconds);
	public long lpush(byte[] key, byte[] strings, int seconds);
	public long lpush(String key, T obj, int seconds);
	public byte[] rpop(byte[] key, int seconds);
	
	/**
	 * set的操作
	 * @param key
	 * @param field
	 * @param seconds
	 * @return
	 */
	public long sadd(String key, String field, int seconds );
	public long sadd(String key, String[] fields, int seconds );
	public long srem(String key, String member, int seconds );
	public String spop(String key, int seconds );
	public long scard(String key);
	public boolean sismember(String key, String member);
	public Set<String> smembers(String key);
	
	/**
	 *设置有效期
	 */
	public void expire(String key, int seconds);
	
	/**
	 *判断key是否存在
	 */
	public boolean exists(String key);
	
	/**
	 * 可以存储对象的map相关操作
	 */
	/**
	 * 存储对象
	 */
	public boolean hsetObject(String key, String field, T object, int seconds);
	/**
	 * 可以存储对象的map相关操作
	 */
	/**
	 * 存储对象
	 */
	public boolean hsetObjectNoGzip(String key, String field, T object, int seconds);

	/**
	 * 批量添加hashset元素
	 * @param key
	 * @param map
	 * @param seconds
	 * @return
	 */
	public boolean hsetObjectList(String key, Map<String, T> map, int seconds);
	/**
	 * 批量添加hashset元素
	 * @param key
	 * @param map
	 * @param seconds
	 * @return
	 */	
	public boolean hsetObjectListNoGzip(String key, Map<String, T> map, int seconds);
	/**
	 * 获取对象
	 */
	public T hgetObject(String key, String field, int seconds);
	/**
	 * 获取对象
	 */
	public T hgetObjectNoGzip(String key, String field, int seconds);

	/**
	 * 获取所有的对象
	 */
	public Map<String, T> hgetAllObject(String key, int seconds);
	/***
	 * 获取剩余过期时间
	 * @param key
	 * @return
	 */
	public int ttl(String key);
	/**
	 * 反垃圾用户有效期设置(时间可变的)
	 *设置有效期
	 */
	public void expireSpamUser(String key, int seconds);
	/**
	 * 获取hashmap中key的数量
	 * @param key
	 * @return
	 */
	public long hkeys(String key);
	
	public Set<String> hGetKeys(String key);
	/***
	 * 获取剩余过期时间
	 * @param key
	 * @return
	 */
	public long ttl_rest(String key);
	/***
	 * zset获取有序集 key 中成员 member 的排名
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrank(String key,String member,int seconds);
	/***
	 * 可以存储对象的zset相关操作
	 * 存储对象
	 * @param key
	 * @param score
	 * @param member
	 * @param seconds
	 * @return
	 */
	public long zaddObject(String key, long score, T object, int seconds);
	/***
	 * 可以存储对象的zset相关操作
	 * 获得对象
	 * @param key
	 * @param start
	 * @param end
	 * @param seconds
	 * @return
	 */
	public Set<T> zrangeObject(String key, long start, long end, int seconds);

	/**
	 * 管道方法hgetall
	 * @param prefixKey
	 * @param suffixKeys
	 * @return
	 */
	List<Object> pipelineHgetall(String prefixKey, Collection<String> suffixKeys);
	
	/**
	 * 管道方法hsetall
	 * @param prefix
	 * @param values
	 */
	void pipelineHsetall(String prefix, Map<String, Map<byte[], byte[]>> values);

	/**
	 * 管道方法判断key是否存在
	 * @param prefixKey
	 * @param suffixKeys
	 * @return List<Boolean>
	 */
	List<Object> pipelineExist(String prefixKey, Collection<String> suffixKeys);

	T getObjFromBytes(byte[] data);

	byte[] getBytesFromObj(T obj);

}
