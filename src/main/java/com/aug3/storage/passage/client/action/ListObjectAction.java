package com.aug3.storage.passage.client.action;

import java.util.List;

import org.apache.thrift.TException;

import com.aug3.storage.passage.thrift.PassageService.Client;
import com.aug3.storage.passage.thrift.Strategy;

public class ListObjectAction implements Action {

    private Strategy strategy;

    private List<String> key;

    @Override
    public Object perform(Client client) throws TException {
        return client.listObject(strategy, key);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<String> getKey() {
        return key;
    }

    public void setKey(List<String> key) {
        this.key = key;
    }

}
