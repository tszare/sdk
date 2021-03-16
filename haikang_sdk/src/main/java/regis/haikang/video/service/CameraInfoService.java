package regis.haikang.video.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.jmx.snmp.Timestamp;
import regis.haikang.API;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import regis.common.mongo.base.BaseServiceImpl;
import regis.haikang.video.HaiKangCameraInfo;
import regis.haikang.video.dao.CameraInfoDao;
import regis.http.client.RegisUtil;

@Service
public class CameraInfoService extends BaseServiceImpl<String, HaiKangCameraInfo, CameraInfoDao> {


    /**
     修改其中一条或者多条属性
     */
    public void modify(String indexCode, String target, String property) {
        Query query = new Query();
        log.info("Test is " + indexCode);
        if (RegisUtil.isNotBlank(indexCode)) {
            query.addCriteria(Criteria.where("indexCode").is(indexCode));
            dao.bulkUpdate(query, Update.update(target, property));
        }
    }


    @Override
    protected Query uniqueQuery(HaiKangCameraInfo data) {
        Query query = new Query();
        return query;
    }



    /**
     * 根据唯一识别号删除对信息
     */
    public void deleteInfo(String indexCode) {
        Query query = new Query();

        if (RegisUtil.isNotBlank(indexCode)) {
            query.addCriteria(Criteria.where("indexCode").is(indexCode));
            dao.bulkDelete(query);
        }
    }




    @Override
    public HaiKangCameraInfo update(HaiKangCameraInfo entity) {
        return null;
    }




    /**
     * 发现数据库内没有存的新的相机信息并保存
     */
    public List<JSONObject> findAndSaveNewDev() {

        Integer devNum = API.totalNum();
        List<JSONObject> newDevice = new ArrayList();
        BigDecimal a = new BigDecimal(devNum);
        BigDecimal b = new BigDecimal(999);
        double c = a.divide(b,3, BigDecimal.ROUND_DOWN).doubleValue();
        for (int i = 0; i <c ; i++) {
            String info = API.devicesInfo(i+1, 999);
            JSONObject cameraData = JSONObject.parseObject(info).getJSONObject("data");
            JSONArray jsonArray = cameraData.getJSONArray("list");

            for (Object o : jsonArray) {
                String proPerties = o.toString();
                JSONObject jProperty = JSONObject.parseObject(proPerties);
                Query query = new Query();
                query.addCriteria(Criteria.where("indexCode").is(jProperty.get("indexCode").toString()));
                HaiKangCameraInfo device = dao.findOne(query);
                if (device == null) {
                    newDevice.add(jProperty);
                    HaiKangCameraInfo entity = new HaiKangCameraInfo();


                    Timestamp d = new Timestamp(System.currentTimeMillis());
                    String codePart1 = String.valueOf(d.getSysUpTime());
                    Random random = new Random();
                    String codePart2 = String.valueOf(random.nextInt(100000));
                    String codePart3 = "Hk";
                    String specialCode = codePart3 + codePart1 + codePart2;

                    entity.setSpecialCode(specialCode);
                    entity.setName(jProperty.get("name").toString());
                    entity.setIndexCode(jProperty.get("indexCode").toString());
                    entity.setParentIndexCode(jProperty.get("parentIndexCode").toString());
                    entity.setCameraType(Integer.valueOf(jProperty.get("cameraType").toString()));
                    entity.setDecodeTag(jProperty.get("decodeTag").toString());
                    entity.setCreateTime(jProperty.get("createTime").toString());
                    entity.setUpdateTime(jProperty.get("updateTime").toString());
                    entity.setTransType(jProperty.get("transType").toString());
                    entity.setTreatyType(jProperty.get("treatyType").toString());
                    entity.setRegionIndexCode(jProperty.get("regionIndexCode").toString());
                    entity.setRegionName(jProperty.get("regionName").toString());
                    entity.setRegionPath(jProperty.get("regionPath").toString());
                    entity.setRegionPathName(jProperty.get("regionPathName").toString());
                    dao.insert(entity);
                }
            }
        }

        return newDevice;
    }




    /**
     * 模糊查询
     * target    查询对象
     * property  查询属性（非精确属性）
     */

    public Page<HaiKangCameraInfo> findConfuse(String target, String property,int page) {
        int pageSize = 100;
        Sort sort = Sort.by(Sort.Direction.ASC, "indexCode");
        Pageable pageable = PageRequest.of(page, pageSize,sort);
        Query query = new Query();
        Pattern pattern = Pattern.compile("^.*"+property+".*$", Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where(target).regex(pattern));
        long count1 = dao.count(query);
        List<HaiKangCameraInfo> resultList = dao.find(query.with(pageable));
        return  new PageImpl<HaiKangCameraInfo>(resultList,pageable,count1);


    }



    public void saveInfo(HaiKangCameraInfo cameraInfo) {



        dao.insert(cameraInfo);



    }


    /**
     * 精确查询
     */
    public HaiKangCameraInfo find(String specialCode){
        Query query = new Query();
        query.addCriteria(Criteria.where("specialCode").is(specialCode));
        HaiKangCameraInfo specificCamera =dao.findOne(query);
        return specificCamera;


    }



}


