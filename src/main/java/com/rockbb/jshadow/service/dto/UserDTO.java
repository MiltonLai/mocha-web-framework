package com.rockbb.jshadow.service.dto;

import java.util.Date;

public class UserDTO {
    private int memberUid;
    private String memberName;
    private String title;
    private String cellphone;
    private Date createTime;

    public int getMemberUid() {return memberUid;}
    public void setMemberUid(int memberUid) {this.memberUid = memberUid;}
    public String getMemberName() {return memberName;}
    public void setMemberName(String memberName) {this.memberName = memberName;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getCellphone() {return cellphone;}
    public void setCellphone(String cellphone) {this.cellphone = cellphone;}
    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}
}
