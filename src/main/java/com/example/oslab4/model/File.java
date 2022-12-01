package com.example.oslab4.model;

import org.jetbrains.annotations.NotNull;

public class File extends Directory {
  private final int Size;

  public File(int Size, @NotNull String Name) {
    super(Name);
    this.Size = Size;
  }
  public int getSize() {
    return Size;
  }
}
