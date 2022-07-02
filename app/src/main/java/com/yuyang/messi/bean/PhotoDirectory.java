package com.yuyang.messi.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PhotoDirectory {

  private String id;
  private ImageBean coverImageBean;
  private String name;
  private long   dateAdded;
  private List<ImageBean> imageBeans = new ArrayList<>();

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PhotoDirectory)) return false;

    PhotoDirectory directory = (PhotoDirectory) o;

    boolean hasId = !TextUtils.isEmpty(id);
    boolean otherHasId = !TextUtils.isEmpty(directory.id);

    if (hasId && otherHasId) {
      if (!TextUtils.equals(id, directory.id)) {
        return false;
      }

      return TextUtils.equals(name, directory.name);
    }

    return false;
  }

  @Override public int hashCode() {
    if (TextUtils.isEmpty(id)) {
      if (TextUtils.isEmpty(name)) {
        return 0;
      }

      return name.hashCode();
    }

    int result = id.hashCode();

    if (TextUtils.isEmpty(name)) {
      return result;
    }

    result = 31 * result + name.hashCode();
    return result;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ImageBean getCoverImageBean() {
    return coverImageBean;
  }

  public void setCoverImageBean(ImageBean coverImageBean) {
    this.coverImageBean = coverImageBean;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  public List<ImageBean> getImageBeans() {
    return imageBeans;
  }

  public void setImageBeans(List<ImageBean> imageBeans) {
    this.imageBeans = imageBeans;
  }
}
