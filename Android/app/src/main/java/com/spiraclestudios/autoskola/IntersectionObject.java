/*
 * Copyright (c) 2015-2017. Spiracle Studios. All Rights Reserved.
 */

package com.spiraclestudios.autoskola;

/**
 * Added by benji on 15/1/2016.
 */
public class IntersectionObject {

  private String path = "";
  private int x;
  private int y;
  private float rot;

  public IntersectionObject(String path, int x, int y, float rot) {
    setPath(path);
    setX(x);
    setY(y);
    setRot(rot);
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getX() {
    return x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getY() {
    return y;
  }

  public void setRot(float rot) {
    this.rot = rot;
  }

  public float getRot() {
    return rot;
  }
}