package com.aug3.storage.passage.server;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.thrift.TException;

import com.aug3.storage.passage.server.handler.HafsHandler;
import com.aug3.storage.passage.server.handler.RequestHandler;
import com.aug3.storage.passage.server.handler.S3Handler;
import com.aug3.storage.passage.thrift.PassageService;
import com.aug3.storage.passage.thrift.SObject;
import com.aug3.storage.passage.thrift.Strategy;

public class PassageServiceImpl implements PassageService.Iface {

	@Override
	public boolean putObject(Strategy strategy, SObject sObj) throws TException {
		getRequestHandler(strategy).putObject(strategy.getBucketName(), sObj.getKey(), sObj.getData());
		return true;
	}

	@Override
	public SObject getObject(Strategy strategy, String key) throws TException {
		return getRequestHandler(strategy).getObject(strategy.getBucketName(), key);
	}

	@Override
	public List<SObject> listObject(Strategy strategy, List<String> key) throws TException {
		return getRequestHandler(strategy).listObject(strategy.getBucketName(), key);
	}

	@Override
	public boolean deleteObject(Strategy strategy, String key) throws TException {
		return getRequestHandler(strategy).deleteObject(strategy.getBucketName(), key);
	}

	@Override
	public boolean isObjectInBucket(Strategy strategy, String key) throws TException {
		return getRequestHandler(strategy).isObjectInBucket(strategy.getBucketName(), key);
	}

	private RequestHandler getRequestHandler(Strategy strategy) {
		switch (strategy.sType) {
		case HAFS:
			return new HafsHandler();
		case S3:
			return new S3Handler();
		default:
			throw new InvalidParameterException("");
		}
	}

}
