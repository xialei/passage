package com.aug3.storage.passage.client.action;

import org.apache.thrift.TException;

import com.aug3.storage.passage.thrift.PassageService;

public interface Action {

    public Object perform(PassageService.Client client) throws TException;

}
