package kr.ac.yuhan.cs.admin.data;

public class AdminData {
    private int adminNum;
    private String adminId;
    private String adminPw;
    private String adminPosition;

    public AdminData(int adminNum, String adminId, String adminPw, String adminPosition) {
        this.adminNum = adminNum;
        this.adminId = adminId;
        this.adminPw = adminPw;
        this.adminPosition = adminPosition;
    }

    public int getAdminNum() {
        return adminNum;
    }

    public void setAdminNum(int adminNum) {
        this.adminNum = adminNum;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminPw() {
        return adminPw;
    }

    public void setAdminPw(String adminPw) {
        this.adminPw = adminPw;
    }

    public String getAdminPosition() {
        return adminPosition;
    }

    public void setAdminPosition(String adminPosition) {
        this.adminPosition = adminPosition;
    }
}
