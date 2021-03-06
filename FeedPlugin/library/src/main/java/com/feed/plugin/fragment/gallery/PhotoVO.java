package com.feed.plugin.fragment.gallery;

public class PhotoVO {

    private String imgPath;
    private boolean selected;
    private int selectCount;

    public PhotoVO(String imgPath, boolean selected) {
        this.imgPath = imgPath;
        this.selected = selected;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSelectCount(){
        return selectCount;
    }

    public void setSelectCount(int selectCount){
        this.selectCount = selectCount;
    }
}
