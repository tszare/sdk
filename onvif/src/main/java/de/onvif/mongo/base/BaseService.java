package de.onvif.mongo.base;

import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.List;

public interface BaseService<A extends Serializable, T extends Identifier<A>> {

	/**
	 * Create the given entity
	 * 
	 * @param entity
	 * @return Created entity
	 */
	T create(T entity);

	/**
	 * Delete the given entity from database
	 * 
	 * @param id
	 */
	void delete(A id);

	/**
	 * Delete the given entity from database
	 * 
	 * @param entity
	 */

	void delete(T entity);

	/**
	 * Update the given entity
	 * 
	 * @param entity
	 * @return Updated entity
	 */
	T update(T entity);

	/**
	 * Find entity by id
	 * 
	 * @param id
	 * @return
	 */
	T findById(A id);

	/**
	 * Find all entities
	 * 
	 * @return Entity list
	 */
	List<T> findAll();

	boolean exists(Query query);

	List<T> findList(Query query);

	/**
	 * Find entities by pagination
	 * 
	 * @param page
	 * @return
	 */
	Pagination<T> findPaging(Pagination<T> page);
}
