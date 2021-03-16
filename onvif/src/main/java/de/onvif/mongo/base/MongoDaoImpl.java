package de.onvif.mongo.base;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import regis.http.client.RegisUtil;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class MongoDaoImpl<A extends Serializable, T extends Identifier<A>> implements MongoFileDao<A, T> {
    protected Log log = LogFactory.getLog(this.getClass());
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    protected GridFsTemplate gridFsTemplate;

    @Override
    public String getCollectionName() {
        if (getEntityClass() == null || getEntityClass().getSimpleName() == null) {
            return null;
        }
        String defaultCollectionName = getEntityClass().getSimpleName().toString();
        return defaultCollectionName.replaceFirst(defaultCollectionName.substring(0, 1),
                defaultCollectionName.substring(0, 1).toLowerCase());
    }

    // public List<String> distinct(final String fieldName, Query query) {
    // if (RegisUtil.isBlank(fieldName)) {
    // return null;
    // }
    // List<String> list =
    // this.mongoTemplate.getCollection(getCollectionName()).distinct(fieldName,
    // query.getQueryObject());
    // log.info(list);
    // return list;
    // }

    @Override
    public void insert(T t) {
        // log.info("before getCollectionName is " + getCollectionName());
        mongoTemplate.insert(t,getCollectionName() );
        // log.info("after getCollectionName is " + getCollectionName());
    }

    @Override
    public void upsert(Query query, Update update) {
        mongoTemplate.upsert(query, update, getEntityClass(), getCollectionName());
    }

    @Override
    public void update(T t) {
        mongoTemplate.save(t, getCollectionName());
    }

    @Override
    public void update(Query query, Update update) {
        mongoTemplate.updateFirst(query, update, getEntityClass(), getCollectionName());
    }

    @Override
    public T findAndModify(Query query, Update update, FindAndModifyOptions options) {
        T t = (T) mongoTemplate.findAndModify(query, update, options, getEntityClass(), getCollectionName());
        return t;
    }

    @Override
    public T findAndModify(Query query, Update update) {
        T t = (T) mongoTemplate.findAndModify(query, update, getEntityClass(), getCollectionName());
        return t;
    }

    @Override
    public long bulkUpdate(Query query, Update update) {
        UpdateResult wr = mongoTemplate.updateMulti(query, update, getEntityClass(), getCollectionName());
        return wr.getModifiedCount();
    }

    @Override
    public void bulkInsert(List<T> entities) {
        mongoTemplate.insert(entities, getCollectionName());
    }

    @Override
    public boolean delete(A id) {
        Query query = new Query(Criteria.where(Identifier.ID).is(id));
        DeleteResult wr = mongoTemplate.remove(query, getEntityClass(), getCollectionName());
        return wr.getDeletedCount() == 1;
    }

    @Override
    public boolean delete(T entity) {
        DeleteResult wr = mongoTemplate.remove(entity, getCollectionName());
        return wr.getDeletedCount() == 1;
    }

    @Override
    public void bulkDelete(Query query) {
        mongoTemplate.remove(query, getEntityClass(), getCollectionName());
    }

    @Override
    public boolean exists(Query q) {
        return mongoTemplate.exists(q, getEntityClass(), getCollectionName());
    }

    @Override
    public T findById(A id) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Identifier.ID).is(id));
        return (T) mongoTemplate.findOne(query, getEntityClass(), getCollectionName());
    }



    @Override
    public T findOne(Query query) {
        T result = (T) mongoTemplate.findOne(query, getEntityClass(), getCollectionName());
        return result;
    }

    @Override
    public List<T> findAll() {
        List<T> list = (List<T>) mongoTemplate.findAll(getEntityClass(), getCollectionName());
        if (list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }

    @Override
    public List<T> find(Query q) {
        List<T> list = (List<T>) mongoTemplate.find(q, getEntityClass(), getCollectionName());
        if (list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }

    @Override
    public Pagination<T> findPaging(Pagination<T> page, Query q) {
        if (q == null) {
            q = new Query();
        }
        long totalCount = mongoTemplate.count(q, getCollectionName());
        if (totalCount == 0) {
            page.setTotalAmount(0);
            page.setResults(Collections.EMPTY_LIST);
        } else {
            page.setTotalAmount(totalCount);
            if (RegisUtil.isBlank(page.getNext())) {
                q = q.limit(page.getPageSize()).skip(page.offset());
            } else {
                q = q.limit(page.getPageSize());
            }
            page.setResults(find(q));
        }
        return page;
    }

    @Override
    public long count(Query q) {
        return mongoTemplate.count(q, getCollectionName());
    }

    @Override
    public <S> MapReduceResults<S> mapReduce(Query q, String inputCollectionName, String mapFunction,
                                             String reduceFunction, String finalizeFunction, Class<S> entityClass) {
        return mongoTemplate.mapReduce(q, inputCollectionName, mapFunction, reduceFunction,
                MapReduceOptions.options().finalizeFunction(finalizeFunction).outputTypeInline(), entityClass);
    }

    @Override
    public ObjectId storeFile(InputStream inputStream, DBObject metaData) {
        return gridFsTemplate.store(inputStream, metaData);
    }

    @Override
    public ObjectId storeFile(InputStream inputStream, String fileName, DBObject metaData) {
        return gridFsTemplate.store(inputStream, fileName, metaData);
    }

    @Override
    public List<GridFSFile> findFile(Query query) {
        GridFSFindIterable iter = gridFsTemplate.find(query);
        if (iter == null) {
            return Collections.emptyList();
        } else {
            List<GridFSFile> list = Lists.newArrayList();
            while (iter.iterator().hasNext()) {
                GridFSFile gridFile = iter.iterator().next();
                list.add(gridFile);
            }
            return list;
        }

    }

    @Override
    public GridFSFile findOneFile(Query query) {
        GridFSFile result = gridFsTemplate.findOne(query);
        return result;
    }

    @Override
    public void deleteFile(Query query) {
        gridFsTemplate.delete(query);
    }
}





