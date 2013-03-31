package com.aug3.storage.passage.client.action;

import org.apache.thrift.TException;

import com.aug3.storage.passage.thrift.PassageService.Client;
import com.aug3.storage.passage.thrift.Strategy;

public class IsObjectExistAction implements Action {

	private Strategy strategy;
	private String key;

	@Override
	public void perform(Client client) throws TException {
		client.isObjectInBucket(strategy, key);
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
