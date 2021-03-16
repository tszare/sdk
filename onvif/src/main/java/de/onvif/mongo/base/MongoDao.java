package de.onvif.mongo.base;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.util.List;

public interface MongoDao<A extends Serializable, T extends Identifier<A>> {
	/**
	 * Get entity class type
	 * 
	 * @return
	 */
	Class<?> getEntityClass();

	/**
	 * Get collectionName in DB
	 * 
	 * @return
	 */
	String getCollectionName();

	/**
	 * Insert entity into database
	 * 
	 * @param t
	 */
	void insert(T t);

	/**
	 * Update / Insert entity into database
	 * 
	 */
	void upsert(Query query, Update update);

	/**
	 * Delete entity from database
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(A id);

	/**
	 * Delete entity from database
	 * 
	 * @param entity
	 * @return
	 */
	boolean delete(T entity);

	/**
	 * Delete entities by query from database
	 * 
	 * @param query
	 * @return
	 */
	void bulkDelete(Query query);

	/**
	 * Update given fields by conditions
	 * 
	 * @param query
	 *            query conditions
	 * @param update
	 *            update field/value map
	 * @return success/fail
	 */
	void update(Query query, Update update);

	/**
	 * findAndModify
	 * 
	 * @param query
	 *            query conditions
	 * @param update
	 *            update field/value map
	 * @return success/fail
	 */
	T findAndModify(Query query, Update update);

	/**
	 * findAndModify
	 * 
	 * @param query
	 *            query conditions
	 * @param update
	 *            update field/value map
	 * @param options
	 * @return success/fail
	 */
	T findAndModify(Query query, Update update, FindAndModifyOptions options);

	/**
	 * Bulk update given fields by conditions
	 * 
	 * @param query
	 *            query conditions
	 * @param update
	 *            update field/value map
	 * @return effect row
	 */
	long bulkUpdate(Query query, Update update);

	/**
	 * Update given entity
	 * 
	 * @param t
	 */
	void update(T t);

	/**
	 * Check whether entity exists or not
	 * 
	 * @param query
	 *            conditions
	 * @return exists/not
	 */
	boolean exists(Query query);

	/**
	 * Find entity by id
	 * 
	 * @param id
	 * @return
	 */
	T findById(A id);

	/**
	 * Find entity by query
	 * 
	 * @param query
	 * @return
	 */
	T findOne(Query query);

	/**
	 * Find all entities
	 * 
	 * @return list
	 */
	List<T> findAll();

	/**
	 * Find entities by conditions
	 * 
	 * @param query
	 *            conditions
	 * @return list
	 */
	List<T> find(Query query);

	/**
	 * Find entities pagination result by conditions
	 * 
	 * @param page
	 * @param query
	 *            conditions
	 * @return
	 */
	Pagination<T> findPaging(Pagination<T> page, Query query);

	/**
	 * Count entity by conditions
	 * 
	 * @param query
	 * @return count
	 */
	long count(Query query);

	/**
	 * bulk insert given collection
	 * 
	 * @param entities
	 *            given collection
	 */
	void bulkInsert(List<T> entities);

	/**
	 * 
	 * @param q
	 *            to filter documents
	 * @param inputCollectionName
	 *            collection needs to be handled
	 * @param mapFunction
	 *            map function, can use a string or a js path
	 * @param reduceFunction
	 *            reduce function, can use a string or a js path
	 * @param finalizeFunction
	 *            finalize function, format the output structure for a document, can use a string or a js path
	 * @param entityClass
	 * @return
	 */
	<S> MapReduceResults<S> mapReduce(Query q, String inputCollectionName, String mapFunction, String reduceFunction,
			String finalizeFunction, Class<S> entityClass);

}
