package regis.haikang.video.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import regis.common.mongo.base.MongoDaoImpl;
import regis.haikang.video.HaiKangCameraInfo;

@Repository
public class CameraInfoDao extends MongoDaoImpl<String, HaiKangCameraInfo> {

    @Override
    public Class<?> getEntityClass() {
        return HaiKangCameraInfo.class;
    }

    public List<HaiKangCameraInfo> findByDeviceIdAndGsafeKey(Query q) {
        @SuppressWarnings("unchecked")
        List<HaiKangCameraInfo> list = (List<HaiKangCameraInfo>) mongoTemplate.find(q, getEntityClass(), getCollectionName());
        if (list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }
}
