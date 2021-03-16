package pl.platform.mongo.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.platform.Info.PlatformCameraInfo;

import pl.platform.Info.ProxyAddressInfo;
import pl.platform.mongo.base.MongoDaoImpl;
import regis.http.client.RegisUtil;


@Repository
public class PlatformCameraInfoDao extends MongoDaoImpl<String, PlatformCameraInfo> {
    @Override
    public Class<?> getEntityClass() { return PlatformCameraInfo.class; }


    public void catchNewDeviceFormOneProxy (String proxyIp, String proxyPort, String category,String specialCode){
        if (!mongoTemplate.collectionExists("platformCameraInfo")){
            mongoTemplate.createCollection("platformCameraInfo");
        }
        String proxyHost = "http://"+proxyIp+":"+proxyPort;
        PlatformCameraInfo platformCameraInfo = new PlatformCameraInfo();

        Query query = new Query();
        query.addCriteria(Criteria.where("specialCode").is(specialCode));

        if(mongoTemplate.findOne(query, getEntityClass(), getCollectionName())==null){
            platformCameraInfo.setProxyHost(proxyHost);
            platformCameraInfo.setCategory(category);
            platformCameraInfo.setSpecialCode(specialCode);
            mongoTemplate.insert(platformCameraInfo);
        }
    }


    public PlatformCameraInfo find(String specialCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("specialCode").is(specialCode));
        PlatformCameraInfo PlatformCameraInfo = (PlatformCameraInfo) mongoTemplate.findOne(query,getEntityClass());
        log.info(RegisUtil.toJson(PlatformCameraInfo));
        return PlatformCameraInfo;
    }






}





