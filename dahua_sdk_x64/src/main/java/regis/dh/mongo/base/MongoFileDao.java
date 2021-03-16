package regis.dh.mongo.base;

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public interface MongoFileDao<A extends Serializable, T extends Identifier<A>> extends MongoDao<A, T> {

	/**
	 * Store file with metaData
	 * 
	 * @param inputStream
	 * @param metadata
	 * @return GridFSFile
	 */
	ObjectId storeFile(InputStream inputStream, DBObject metadata);

	/**
	 * Store file with fileName, metaData
	 * 
	 * @param inputStream
	 * @param fileName
	 * @param metadata
	 * @return GridFSFile
	 */
	ObjectId storeFile(InputStream inputStream, String fileName, DBObject metadata);

	/**
	 * Get files by conditions
	 * 
	 * @param query
	 *            conditions
	 * @return list<GridFSDBFile>
	 */
	List<GridFSFile> findFile(Query query);

	/**
	 * Get file by conditions
	 * 
	 * @param query
	 *            conditions
	 * @return GridFSDBFile
	 */
	GridFSFile findOneFile(Query query);

	/**
	 * Delete file by conditions
	 * 
	 * @param query
	 *            conditions
	 * @return
	 */
	void deleteFile(Query query);

}
