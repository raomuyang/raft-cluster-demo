package cn.atomicer.zephyr.zk;

import org.apache.zookeeper.data.Stat;

import java.util.Date;

/**
 * @author Rao Mengnan
 *         on 2018/5/11.
 */
public class DataContent {
    private byte[] data;
    private Stat stat;

    private Date createTime;
    private Date updateTime;
    private long czxid;
    private long mzxid;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public long getCzxid() {
        return czxid;
    }

    public void setCzxid(long czxid) {
        this.czxid = czxid;
    }

    public long getMzxid() {
        return mzxid;
    }

    public void setMzxid(long mzxid) {
        this.mzxid = mzxid;
    }

    @Override
    public String toString() {
        return "DataContent{" +
                "stat=" + stat +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", czxid=" + czxid +
                ", mzxid=" + mzxid +
                '}';
    }
}
