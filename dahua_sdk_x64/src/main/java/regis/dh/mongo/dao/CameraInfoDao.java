package regis.dh.mongo.dao;


import regis.dh.Info.DaHuaCameraInfo;
import regis.dh.mongo.base.MongoDaoImpl;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.Collections;
import java.util.List;

@Repository
public class CameraInfoDao extends MongoDaoImpl<String, DaHuaCameraInfo> {

    @Override
    public Class<?> getEntityClass() {
        return DaHuaCameraInfo.class;
    }

    public List<DaHuaCameraInfo> findByDeviceIdAndGsafeKey(Query q) {
        @SuppressWarnings("unchecked")
        List<DaHuaCameraInfo> list = (List<DaHuaCameraInfo>) mongoTemplate.find(q, getEntityClass(), getCollectionName());
        if (list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }
}
