package regis.dh.mongo.service;


import com.sun.jmx.snmp.Timestamp;
import regis.dh.Info.DaHuaCameraInfo;
import regis.dh.mongo.dao.CameraInfoDao;
import regis.dh.mongo.base.BaseServiceImpl;
import org.dom4j.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import regis.DahuaClient;
import regis.DahuaServer;
import regis.http.client.RegisUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;


@Service
public class DHDataService extends BaseServiceImpl<String, DaHuaCameraInfo, CameraInfoDao> {


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
    public DaHuaCameraInfo find(String specialCode){
        Query query = new Query();
        query.addCriteria(Criteria.where("specialCode").is(specialCode));
        return dao.findOne(query);


    }


    @Override
    protected Query uniqueQuery(DaHuaCameraInfo entity) {
        return null;
    }

    @Override
    public DaHuaCameraInfo update(DaHuaCameraInfo entity) {
        return null;
    }



    /**
     * 模糊查询
     * target    查询对象
     * property  查询属性（非精确属性）
     */

    public Page<DaHuaCameraInfo> findConfuse(String target, String property, int page) {
        int pageSize = 100;
        Sort sort = Sort.by(Sort.Direction.ASC, "indexCode");
        Pageable pageable = PageRequest.of(page, pageSize,sort);
        Query query = new Query();
        Pattern pattern = Pattern.compile("^.*"+property+".*$", Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where(target).regex(pattern));
        long count1 = dao.count(query);
        List<DaHuaCameraInfo> resultList = dao.find(query.with(pageable));

        return  new PageImpl<DaHuaCameraInfo>(resultList,pageable,count1);


    }



    public static int count(String str, char c){

        int k=0;
        for( int i=0;i<str.length();i++){
            if(str.charAt(i)==c)
                k++;
        }
        return k;
    }




    /**
     * 获取当前最新的服务器组织树，并存储下数据库中没有的设备信息
     */

    public void savaData() throws DocumentException {
        DahuaClient app = DahuaServer.app;
        String Info = app.GetDeviceInfo();
        Document document = DocumentHelper.parseText(Info);
        Element root = document.getRootElement();
        Element devices = root.element("Devices");
        Element Department = root.element("Department");
        DaHuaCameraInfo entity = new DaHuaCameraInfo();
        Iterator<Element> it=devices.elementIterator("Device");
        while(it.hasNext()){
            Element elem = it.next();
            String DeviceId = elem.attributeValue("id");
            Element element = (Element) Department.selectSingleNode("//Department//*[@id='"+DeviceId+"' ]");
            int s = count(element.getPath(),'/');
            Element parent = element.getParent();
            String regionPath = "";
            for (int i = 0; i <s-2 ; i++) {
                String sa_dd = parent.attributeValue("name");
                parent=parent.getParent();
                regionPath="/"+sa_dd+regionPath;
            }
            entity.setParentCoding(element.getParent().attributeValue("coding"));
            entity.setParentName(element.getParent().attributeValue("name"));
            entity.setRegionPath(regionPath);
            entity.setDeviceID(DeviceId);
            entity.setServerIP(elem.attributeValue("ip"));
            entity.setName(elem.attributeValue("name"));
            String manufacturer = elem.attributeValue("manufacturer");
            Timestamp d = new Timestamp(System.currentTimeMillis());
            String codePart1 =String.valueOf(d.getSysUpTime());
            Random random = new Random();
            String codePart2 =String.valueOf(random.nextInt(100000));
            String codePart3="daHua";
            String specialCode = codePart3+codePart1+codePart2;
            entity.setSpecialCode(specialCode);
            entity.setId(codePart1+codePart2);
            if(manufacturer.equals("1")){
                entity.setManufacturer("DaHua");
            }else if(manufacturer.equals("2")){
                entity.setManufacturer("HaiKang");
            }else {
                entity.setManufacturer("Unknown");
            }

            entity.setDeviceIP(elem.attributeValue("deviceIp"));
            entity.setDevicePort(elem.attributeValue("devicePort"));
            entity.setUser(elem.attributeValue("user"));
            entity.setPassword(elem.attributeValue("password"));
            Query query =new Query();


            query.addCriteria(Criteria.where("DeviceID").is(elem.attributeValue("id")));
            DaHuaCameraInfo cameraInfo= dao.findOne(query);
            if(cameraInfo==null){
                dao.insert(entity);
            }



            System.out.println(elem.attributeValue("ip"));
        }





    }

}


