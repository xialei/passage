namespace java com.aug3.storage.passage.thrift

enum Storage {
	S3,
	HAFS
}

struct Strategy {
	1:required Storage sType,
	2:required string bucketName,
}

struct SObject {
	1:required string key,
	2:required binary data
}

service PassageService {
	bool putObject(1: Strategy strategy, 2: SObject sObj),
	SObject getObject(1: Strategy strategy, 2: string key),
	list<SObject> listObject(1: Strategy strategy, 2: list<string> key),
	bool deleteObject(1: Strategy strategy, 2: string key),
	bool isObjectInBucket(1: Strategy strategy, 2: string key),
	bool createImg(1: Strategy strategy, 2: string key)
}
