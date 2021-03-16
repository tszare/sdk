package pl.platform.mongo.dao;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.platform.Info.ProxyAddressInfo;
import pl.platform.mongo.base.MongoDaoImpl;

import org.springframework.data.mongodb.core.query.Query;
import regis.http.client.RegisUtil;


@Repository
public class ProxyAddressInfoDao extends MongoDaoImpl<String, ProxyAddressInfo> {

    @Override
    public Class<?> getEntityClass() { return ProxyAddressInfo.class; }

    public void catchNewProxyAddress(String proxyIp,String proxyPort,String category) {

        if (!mongoTemplate.collectionExists("proxyAddressInfo")){
            mongoTemplate.createCollection("proxyAddressInfo");
        }
        ProxyAddressInfo proxyAddressInfo = new ProxyAddressInfo();
        Query query = new Query();
        query.addCriteria(Criteria.where("proxyIp").is(proxyIp).and("proxyPort").is(proxyPort));

        if(mongoTemplate.findOne(query, getEntityClass(), getCollectionName())==null){
            proxyAddressInfo.setProxyIp(proxyIp);
            proxyAddressInfo.setProxyPort(proxyPort);
            proxyAddressInfo.setName("未命名");
            proxyAddressInfo.setCategory(category);
            mongoTemplate.insert(proxyAddressInfo);
        }
    }

    /**
     对proxyServer命名或修改命名
     */
    public String  setProxyName(String proxyIp,String proxyPort,String name) {
        Query query = new Query();

        query.addCriteria(Criteria.where("proxyIp").is(proxyIp).and("proxyPort").is(proxyPort));
        Update update =new Update().set("name",name);
        UpdateResult setNameResult = mongoTemplate.updateFirst(query,update,getEntityClass());
        log.info(setNameResult);
        return setNameResult.toString();
    }


    /**
     精准查询
     */
    public ProxyAddressInfo findAddress(String proxyIp,String proxyPort) {
        Query query = new Query();
        query.addCriteria(Criteria.where("proxyIp").is(proxyIp).and("proxyPort").is(proxyPort));
        ProxyAddressInfo proxyAddress = (ProxyAddressInfo) mongoTemplate.findOne(query,getEntityClass());
        log.info(RegisUtil.toJson(proxyAddress));
        return proxyAddress;
    }




}
