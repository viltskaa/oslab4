package com.example.oslab4.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Cluster implements Cloneable {
  private String Name;
  private ArrayList<Cluster> nextClusters;
  private int Size;

  private Cluster() {}

  public boolean isRoot() {
    return nextClusters != null;
  }

  public boolean isDirectory() {
    return Size == 0;
  }

  public void add(@NotNull Cluster cluster) {
    if (nextClusters == null)
      nextClusters = new ArrayList<>();
    nextClusters.add(cluster);
  }

  public void add(@NotNull ArrayList<Cluster> clusters) {
    if (nextClusters == null)
      nextClusters = new ArrayList<>();
    nextClusters.addAll(clusters);
  }

  public boolean remove(@NotNull Cluster cluster) {
    return nextClusters.remove(cluster);
  }

  public @Nullable ArrayList<Cluster> getChildren() {
    return nextClusters;
  }

  public void clearClusterList() {
    if (nextClusters != null)
      nextClusters = new ArrayList<>();
  }

  public String getName() {
    return this.Name;
  }

  public int getSize() {
    return Size;
  }

  protected int getFullSize() {
    int localSize = getSize();
    if (this.nextClusters != null) {
      for (Cluster cluster: this.nextClusters) {
        localSize += cluster.getFullSize();
      }
    }
    return localSize;
  }

  @Override
  public String toString() {
    return String.format("%s (%d)", this.Name, getFullSize());
  }

  public static Builder getBuilder() {
    return new Cluster().new Builder();
  }

  @Override
  public Cluster clone() {
    try {
      return (Cluster) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  public class Builder {
    private Builder() {}
    public Builder setName(@NotNull String name) {
      Cluster.this.Name = name;
      return this;
    }
    public Builder setSize(int size) {
      Cluster.this.Size = size;
      return this;
    }
    public Cluster build() {
      return Cluster.this;
    }
  }
}
