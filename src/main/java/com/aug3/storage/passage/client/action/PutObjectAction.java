package com.aug3.storage.passage.client.action;

import org.apache.thrift.TException;

import com.aug3.storage.passage.thrift.PassageService.Client;
import com.aug3.storage.passage.thrift.SObject;
import com.aug3.storage.passage.thrift.Strategy;

public class PutObjectAction implements Action {

    private Strategy strategy;
    private SObject sObj;

    @Override
    public Object perform(Client client) throws TException {
        return client.putObject(strategy, sObj);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public SObject getsObj() {
        return sObj;
    }

    public void setsObj(SObject sObj) {
        this.sObj = sObj;
    }

}
