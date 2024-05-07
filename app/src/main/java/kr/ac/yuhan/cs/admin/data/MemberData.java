package kr.ac.yuhan.cs.admin.data;

import java.util.Date;

public class MemberData {
    private int number;
    private String userId;
    private Date joinDate;
    private String uid;
    private int point;

    public MemberData(int number, String userId, Date joinDate, String uid, int point) {
        this.number = number;
        this.userId = userId;
        this.joinDate = joinDate;
        this.uid = uid;
        this.point = point;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Date getJoinDate() {
        return joinDate;
    }
    public void setJoinDate() {
        this.joinDate = joinDate;
    }
    public String getUid(){
        return uid;
    }
    public void setUid(){
        this.uid = uid;
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }
}
