package com.lei.model;

import java.util.Date;

public class Message {
    private String id;

    private String formid;

    private String toid;

    private String content;

    private String conversationId;

    private Date createdDate;

    /**
     * 当前会话的记录数
     */
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 0 ：未读   1：已读
     */
    private Integer hasRead;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid == null ? null : formid.trim();
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid == null ? null : toid.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getConversationId() {
        return String.format("%s_%s", formid, toid);
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId == null ? null : conversationId.trim();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", formid='" + formid + '\'' +
                ", toid='" + toid + '\'' +
                ", content='" + content + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", createdDate=" + createdDate +
                ", count=" + count +
                ", hasRead=" + hasRead +
                '}';
    }
}