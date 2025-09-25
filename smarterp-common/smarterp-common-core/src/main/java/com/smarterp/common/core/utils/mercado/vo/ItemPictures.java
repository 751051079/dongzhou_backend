package com.smarterp.common.core.utils.mercado.vo;

public class ItemPictures {
    /**
     * 美客多图片id
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static ItemPictures getInstance(String id){
        ItemPictures picture = new ItemPictures();
        picture.setId(id);
        return picture;
    }
}
