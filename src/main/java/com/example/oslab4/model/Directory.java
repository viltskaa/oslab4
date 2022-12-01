package com.example.oslab4.model;

import org.jetbrains.annotations.NotNull;

public class Directory {
  protected final String Name;

  public Directory(@NotNull String Name) {
    this.Name = Name;
  }

  public String getName() {
    return Name;
  }

  @Override
  public String toString() {
    return Name;
  }

  public boolean equals(@NotNull Directory other) {
    return other.Name.equals(this.Name);
  }
}
