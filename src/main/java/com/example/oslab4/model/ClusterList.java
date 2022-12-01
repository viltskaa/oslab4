package com.example.oslab4.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ClusterList extends ArrayList<Cluster> {
  public static @NotNull ClusterList getList(final int capacity) {
    ClusterList out = new ClusterList();
    for (int i = 0; i < capacity; i++) out.add(null);
    return out;
  }
  public static @NotNull ClusterList getList(final int capacity, @NotNull final ArrayList<Cluster> list) {
    ClusterList out = new ClusterList();
    out.addAll(list);
    for (int i = out.size(); i < capacity; i++) out.add(null);
    return out;
  }
}
