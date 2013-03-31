package com.aug3.storage.passage.server.handler;

import java.util.List;

import com.aug3.storage.passage.thrift.SObject;

public interface RequestHandler {

	public boolean putObject(String bucketName, String key, byte[] data);

	public SObject getObject(String bucketName, String key);

	public List<SObject> listObject(String bucketName, List<String> key);

	public boolean deleteObject(String bucketName, String key);

	public boolean isObjectInBucket(String bucketName, String key);
	
}
