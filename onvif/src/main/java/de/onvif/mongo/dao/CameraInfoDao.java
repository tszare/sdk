package de.onvif.mongo.dao;

import de.onvif.Info.OnvifCameraInfo;
import de.onvif.mongo.base.MongoDaoImpl;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.Collections;
import java.util.List;

@Repository
public class CameraInfoDao extends MongoDaoImpl<String, OnvifCameraInfo> {

    @Override
    public Class<?> getEntityClass() {
        return OnvifCameraInfo.class;
    }

    public List<OnvifCameraInfo> findByDeviceIdAndGsafeKey(Query q) {
        @SuppressWarnings("unchecked")
        List<OnvifCameraInfo> list = (List<OnvifCameraInfo>) mongoTemplate.find(q, getEntityClass(), getCollectionName());
        if (list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }
}
