package com.cloud.runball.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserImageModel implements Serializable {

  /**
   * user_img_path : https://api-all-sporter.megacombine.com/user_image/2021/04/2021-04-24/ball_desc.png
   * file_path : {"file_path_parent":"user_image/2021/04/2021-04-24","file_path":"user_image/2021/04/2021-04-24/ball_desc.png"}
   */


  private String user_img_path;

  @SerializedName("matchs_img_path")
  private String imgPath;

  private FilePathDTO file_path;

  public String getUser_img_path() {
    return user_img_path;
  }

  public void setUser_img_path(String user_img_path) {
    this.user_img_path = user_img_path;
  }

  public String getImgPath() {
    return imgPath;
  }

  public void setImgPath(String imgPath) {
    this.imgPath = imgPath;
  }

  public FilePathDTO getFile_path() {
    return file_path;
  }

  public void setFile_path(FilePathDTO file_path) {
    this.file_path = file_path;
  }

  public static class FilePathDTO implements Serializable{
    /**
     * file_path_parent : user_image/2021/04/2021-04-24
     * file_path : user_image/2021/04/2021-04-24/ball_desc.png
     */

    private String file_path_parent;
    private String file_path;

    public String getFile_path_parent() {
      return file_path_parent;
    }

    public void setFile_path_parent(String file_path_parent) {
      this.file_path_parent = file_path_parent;
    }

    public String getFile_path() {
      return file_path;
    }

    public void setFile_path(String file_path) {
      this.file_path = file_path;
    }

    @Override
    public String toString(){
      return "file_path_parent="+file_path_parent+";file_path="+file_path;
    }

  }

  @Override
  public String toString(){
    return "user_img_path="+user_img_path+";file_path="+file_path;
  }

}
