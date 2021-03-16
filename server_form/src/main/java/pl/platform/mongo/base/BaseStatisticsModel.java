package pl.platform.mongo.base;

import java.util.Date;

public class BaseStatisticsModel extends Identifier<String> {
    private static final long serialVersionUID = -5119840982912508910L;
    protected Date refDate;
    protected Date createTime;

    public Date getRefDate() {
        return refDate;
    }

    public void setRefDate(Date refDate) {
        this.refDate = refDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
