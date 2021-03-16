package de.onvif.mongo.service;

import de.onvif.Info.OnvifCameraInfo;

import de.onvif.mongo.base.BaseServiceImpl;
import de.onvif.mongo.dao.CameraInfoDao;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import regis.http.client.RegisUtil;

import java.util.List;
import java.util.regex.Pattern;


@Service
public class CameraInfoService extends BaseServiceImpl<String, OnvifCameraInfo, CameraInfoDao> {


    /**
     修改其中一条或者多条属性
     */
    public void modify(String specialCode, String target, String property) {
        Query query = new Query();
        if (RegisUtil.isNotBlank(specialCode)) {
            query.addCriteria(Criteria.where("specialCode").is(specialCode));
            dao.bulkUpdate(query, Update.update(target, property));

        }
    }


    /**
     * 根据唯一识别号删除对信息
     */
    public void deleteInfo(String specialCode) {
        Query query = new Query();

        if (RegisUtil.isNotBlank(specialCode)) {
            query.addCriteria(Criteria.where("specialCode").is(specialCode));
            dao.bulkDelete(query);
        }
    }


    /**
     * 精确查询
     */
    public OnvifCameraInfo find(String specialCode){
        Query query = new Query();
        query.addCriteria(Criteria.where("specialCode").is(specialCode));
        OnvifCameraInfo specificCamera =dao.findOne(query);
        return specificCamera;


    }



    /**
     * 存入数据库
     */
    public  String saveInfo(OnvifCameraInfo onvifCameraInfo) {
        if (onvifCameraInfo==null){
            return "the input CameraInfo is empty";
        }
        dao.insert(onvifCameraInfo);
        return "sava data success";
    }


    @Override
    protected Query uniqueQuery(OnvifCameraInfo entity) {
        return null;
    }

    @Override
    public OnvifCameraInfo update(OnvifCameraInfo entity) {
        return null;
    }








    /**
     * 模糊查询
     * target    查询对象
     * property  查询属性（非精确属性）
     */

    public Page<OnvifCameraInfo> findConfuse(String target, String property, int page) {
        int pageSize = 100;
        Sort sort = Sort.by(Sort.Direction.ASC, "indexCode");
        Pageable pageable = PageRequest.of(page, pageSize,sort);
        Query query = new Query();
        Pattern pattern = Pattern.compile("^.*"+property+".*$", Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where(target).regex(pattern));
        long count1 = dao.count(query);
        List<OnvifCameraInfo> resultList = dao.find(query.with(pageable));
        return  new PageImpl<OnvifCameraInfo>(resultList,pageable,count1);


    }
}


