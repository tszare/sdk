package regis.common.mongo.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import regis.http.client.RegisUtil;

public abstract class BaseServiceImpl<A extends Serializable, T extends Identifier<A>, Dao extends MongoDao<A, T>>
		implements BaseService<A, T> {

	protected Log log = LogFactory.getLog(this.getClass());
	public static final String ORDERING_DESC = "DESC";
	public static final String ORDERING_ASC = "ASC";
	@Autowired
	protected Dao dao;

	@Override
	public T create(T entity) {
		dao.insert(entity);
		return entity;
	}






	@Override
	public void delete(A id) {
		if (dao.delete(id) == false) {
			// throw new IOException(dao.getEntityClass().getSimpleName() + "
			// can not be deleted!");
		}
	}

	@Override
	public void delete(T entity) {
		if (dao.delete(entity) == false) {
			// throw new Exception(dao.getEntityClass().getSimpleName() + " can
			// not be deleted!",
			// MessageKeys.ENTITY_NOT_FOUND_FOR_IDENTIFIER,
			// new Object[] { dao.getEntityClass().getSimpleName(),
			// entity.getId() });
		}
	}

	@Override
	public T findById(A id) {
		T t = dao.findById(id);
		if (t == null) {
			// throw new IOException(dao.getEntityClass().getSimpleName() + "
			// not found!",
			// MessageKeys.ENTITY_NOT_FOUND_FOR_IDENTIFIER,
			// new Object[] { dao.getEntityClass().getSimpleName(), id });
		}
		return t;
	}

	public T getById(A id) {
		T t = dao.findById(id);
		if (t == null) {
			return null;
		} else {
			return t;
		}
	}

	@Override
	public boolean exists(Query query) {
		return dao.exists(query);
	}




	@Override
	public List<T> findAll() {
		return dao.findAll();
	}

	@Override
	public List<T> findList(Query query) {
		return dao.find(query);
	}

	@Override
	public Pagination<T> findPaging(Pagination<T> page) {
		return dao.findPaging(page, new Query());
	}

	protected abstract Query uniqueQuery(T entity);

	public Pagination<T> findPaging(T t, Query query, int pageNum, int pageSize) {
		Pagination<T> page = new Pagination<>();
		if (t == null || pageNum < 0 || pageSize < 0) {
			page.setTotalAmount(0);
			page.setResults(Collections.emptyList());
			return page;
		}
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		if (query == null) {
			query = new Query();
		}
		return dao.findPaging(page, query);
	}

	public T createOrUpdate(T entity) {
		Query query = uniqueQuery(entity);
		List<T> entityList = dao.find(query);
		if (entityList == null || entityList.isEmpty()) {
			// log.info(entity);
			dao.insert(entity);
		} else {
			for (T oldEntity : entityList) {
				A oldId = oldEntity.getId();
				entity.setId(oldId);
				dao.update(entity);
			}
		}
		return entity;
	}

	protected void addNextTimeCriteriaToQuery(Pagination<T> pagination, Query query, String startTime, String endTime) {
		if (RegisUtil.isNotBlank(pagination.getOrderBy())) {
			Criteria timeCriteria = Criteria.where(pagination.getOrderBy());
			if (startTime != null) {
				timeCriteria.gte(new Date(Long.valueOf(startTime)));
			}
			if (endTime != null) {
				timeCriteria.lte(new Date(Long.valueOf(endTime)));
			}
			if (RegisUtil.isNotBlank(pagination.getNext())) {
				if (ORDERING_ASC.equals(pagination.getOrdering())) {
					timeCriteria.gt(new Date(Long.valueOf(pagination.getNext())));
				} else {
					timeCriteria.lt(new Date(Long.valueOf(pagination.getNext())));
				}
			}
			if (RegisUtil.isNotBlank(pagination.getNext()) || startTime != null || endTime != null) {
				query.addCriteria(timeCriteria);
			}
		} else {
			return;
		}
	}

}
