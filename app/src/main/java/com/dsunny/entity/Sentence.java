package com.dsunny.entity;

import java.util.List;

/**
 * 每日一句
 */
public class Sentence {
    /**
     * sid : 每日一句ID
     * tts : 音频地址
     * content : 英文内容
     * note : 中文内容
     * love : 每日一句喜欢个数
     * translation : 词霸小编
     * picture : 图片地址
     * picture2 : 大图片地址
     * caption : 标题
     * dateline : 时间
     * s_pv : 浏览数
     * sp_pv : 语音评测浏览数
     * tags : 相关标签
     * fenxiang_img : 合成图片，建议分享微博用的
     */

    private String sid;
    private String tts;
    private String content;
    private String note;
    private String love;
    private String translation;
    private String picture;
    private String picture2;
    private String caption;
    private String dateline;
    private String s_pv;
    private String sp_pv;
    private String fenxiang_img;
    /**
     * id : 标签ID
     * name : 标签
     */

    private List<TagsEntity> tags;

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public void setS_pv(String s_pv) {
        this.s_pv = s_pv;
    }

    public void setSp_pv(String sp_pv) {
        this.sp_pv = sp_pv;
    }

    public void setFenxiang_img(String fenxiang_img) {
        this.fenxiang_img = fenxiang_img;
    }

    public void setTags(List<TagsEntity> tags) {
        this.tags = tags;
    }

    public String getSid() {
        return sid;
    }

    public String getTts() {
        return tts;
    }

    public String getContent() {
        return content;
    }

    public String getNote() {
        return note;
    }

    public String getLove() {
        return love;
    }

    public String getTranslation() {
        return translation;
    }

    public String getPicture() {
        return picture;
    }

    public String getPicture2() {
        return picture2;
    }

    public String getCaption() {
        return caption;
    }

    public String getDateline() {
        return dateline;
    }

    public String getS_pv() {
        return s_pv;
    }

    public String getSp_pv() {
        return sp_pv;
    }

    public String getFenxiang_img() {
        return fenxiang_img;
    }

    public List<TagsEntity> getTags() {
        return tags;
    }

    public static class TagsEntity {
        private String id;
        private String name;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "TagsEntity{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "sid='" + sid + '\'' +
                ", tts='" + tts + '\'' +
                ", content='" + content + '\'' +
                ", note='" + note + '\'' +
                ", love='" + love + '\'' +
                ", translation='" + translation + '\'' +
                ", picture='" + picture + '\'' +
                ", picture2='" + picture2 + '\'' +
                ", caption='" + caption + '\'' +
                ", dateline='" + dateline + '\'' +
                ", s_pv='" + s_pv + '\'' +
                ", sp_pv='" + sp_pv + '\'' +
                ", fenxiang_img='" + fenxiang_img + '\'' +
                ", tags=" + tags +
                '}';
    }
}
