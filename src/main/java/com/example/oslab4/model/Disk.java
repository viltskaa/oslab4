package com.example.oslab4.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Disk {
  private ArrayList<Cluster> clusterArrayList;
  private int Size;
  private int ClusterSize;
  private int clusterCount;
  private onUpdateFunction updateFunction;

  private Disk() {}

  @Contract(" -> new")
  public static @NotNull Builder getBuilder() {
    return new Disk().new Builder();
  }

  public void onUpdate(onUpdateFunction func) {
    updateFunction = func;
  }

  private void update() {
    if (updateFunction != null) {
      updateFunction.update(clusterArrayList);
    }
  }

  public ArrayList<Cluster> getClusters() {
    return clusterArrayList;
  }

  public int getClusterCount() {
    return clusterCount;
  }

  public int getCurrentSize() {
    return clusterArrayList.size();
  }

  private int getSize(@NotNull Directory object) {
    return object instanceof File ? ((File) object).getSize() : 0;
  }

  @Contract(pure = true)
  private @NotNull String RemoveRootFromPath(@NotNull String path) {
    return path.replace("root/", "");
  }

  public Cluster getClusterByPath(@NotNull String path) {
    return getClusterByPath(clusterArrayList.get(0), path);
  }

  private @Nullable Cluster getClusterByPath(@NotNull Cluster root, @NotNull String path) {
    path = RemoveRootFromPath(path);
    String localPath = path.contains("/") ? path.substring(0, path.indexOf("/")) : path;
    if (root.getName().equals(localPath)) {
      return root;
    }
    if (root.isRoot()) {
      assert root.getChildren() != null;
      for (Cluster child : root.getChildren()) {
        if (child.getName().equals(localPath)) {
          return getClusterByPath(child, path.contains("/") ? path.replace(localPath + "/", "") : path);
        }
      }
    }
    return null;
  }

  private int getCurrentSizeForFile(@NotNull Directory object) {
    if (object instanceof File) {
      return (int) Math.ceil((double) ((File) object).getSize() / this.ClusterSize);
    }
    return 1;
  }

  private void add(@NotNull Cluster cluster) {
    if (clusterArrayList.size() + 1 > clusterCount)
      return;
    clusterArrayList.add(cluster);
    update();
  }

  private void add(ArrayList<Cluster> clusters) {
    if (clusterArrayList.size() + clusters.size() > clusterCount)
      return;
    this.clusterArrayList.addAll(clusters);
    update();
  }

  private void add(@NotNull Directory object) {
    if (clusterArrayList.size() + 1 > clusterCount)
      return;
    clusterArrayList.add(getCluster(object.getName()));
    update();
  }

  public boolean add(@NotNull Directory object, String path) {
    Cluster cluster = getCluster(object.getName(), Math.min(getSize(object), this.ClusterSize));
    Cluster root = getClusterByPath(path);
    if (root == null
        || !root.isDirectory()
        || clusterArrayList.size() + getCurrentSizeForFile(object) > clusterCount)
      return false;

    root.add(cluster);
    add(cluster);
    if (object instanceof File) {
      ArrayList<Cluster> list = FileToClusters((File) object);
      cluster.add(list);
      add(list);
    }
    update();
    return true;
  }

  private Cluster getCluster(@NotNull String name, int size) {
    return Cluster
          .getBuilder()
          .setSize(size)
          .setName(name)
          .build();
  }

  private Cluster getCluster(@NotNull String name) {
    return Cluster
          .getBuilder()
          .setName(name)
          .build();
  }

  private Cluster getCluster(int size) {
    return Cluster
        .getBuilder()
        .setSize(size)
        .build();
  }

  private @NotNull ArrayList<Cluster> FileToClusters(@NotNull File file) {
    int size = file.getSize() - Math.min(file.getSize(), this.ClusterSize);
    int localClusterCount = (int) Math.ceil((double) size / this.ClusterSize);
    ArrayList<Cluster> list = new ArrayList<>();
    for (int i = 0; i < localClusterCount; i++) {
      Cluster node = getCluster(Math.min(size, this.ClusterSize));
      list.add(node);
      size -= node.getSize();
    }
    return list;
  }

  private String getTreeView(@NotNull Cluster root, int indent) {
    StringBuilder output = new StringBuilder();
    output
        .append(new String(new char[indent]).replace("\0", " "))
        .append("- ")
        .append(root)
        .append("\n");
    if (root.isRoot()) {
      assert root.getChildren() != null;
      for (Cluster node : root.getChildren()) {
        if (node.getName() != null)
          output.append(getTreeView(node, indent + 2));
      }
    }
    return String.valueOf(output);
  }

  public Cluster getParent(@NotNull String path) {
    String filename = path.substring(path.lastIndexOf("/"));
    return getClusterByPath(path.replace(filename, ""));
  }

  public boolean move(@NotNull String target, @NotNull String path) {
    Cluster node = getClusterByPath(target);
    Cluster to = getClusterByPath(path);

    if (node == null || to == null || target.equals("root"))
      return false;

    Cluster rootNode = getParent(target);
    rootNode.remove(node);
    to.add(node);

    update();
    return true;
  }

  public @NotNull ArrayList<Cluster> getAllClusters(@NotNull Cluster node) {
    ArrayList<Cluster> out = new ArrayList<>();
    out.add(node);
    if (node.isRoot()) {
      assert node.getChildren() != null;
      for (Cluster child : node.getChildren()) {
        out.addAll(getAllClusters(child));
      }
    }
    return out;
  }

  public boolean remove(@NotNull String target) {
    Cluster toRemove = getClusterByPath(target);
    Cluster parent = getParent(target);

    if (toRemove == null || parent == null)
      return false;


    if (parent.remove(toRemove) && clusterArrayList.removeAll(getAllClusters(toRemove))) {
      update();
      return true;
    }
    return false;
  }

  private @NotNull Cluster copy(@NotNull Cluster node) {
    Cluster out = node.clone();
    out.clearClusterList();
    if (node.isRoot()) {
      assert node.getChildren() != null;
      for (Cluster child : node.getChildren()) {
        out.add(copy(child));
      }
    }
    return out;
  }

  public boolean haveDuplicate(@NotNull Cluster to, @NotNull Cluster copy) {
    if (to.isRoot()) {
      assert to.getChildren() != null;
      for (Cluster child: to.getChildren()) {
        if (copy.getName().equals(child.getName()))
          return true;
      }
    }
    return false;
  }

  private int getFullSize() {
    int sum = 0;
    for (Cluster node: clusterArrayList) {
      sum += node.getSize();
    }
    return sum;
  }

  public boolean copy(@NotNull String target, @NotNull String to) {
    Cluster toCopy = getClusterByPath(target),
            toPaste = getClusterByPath(to);

    if (toCopy == null
        || toPaste == null
        || !toPaste.isDirectory()
        || target.equals(to)
        || toCopy.getFullSize() + getFullSize() > this.Size) {
      return false;
    }

    Cluster copy = copy(toCopy);
    if (!haveDuplicate(toPaste, copy)) {
      toPaste.add(copy);
      add(getAllClusters(copy));
      update();
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return getTreeView(clusterArrayList.get(0), 0);
  }

  public class Builder {
    private Builder() {}
    public Builder setSize(int size) {
      if (size > 0) {
        Disk.this.Size = size;
        return this;
      }
      return null;
    }
    public Builder setClusterSize(int size) {
      if (size > 0 && size < Disk.this.Size) {
        Disk.this.ClusterSize = size;
        return this;
      }
      return null;
    }
    public Disk build() {
      Disk.this.clusterCount = Disk.this.Size / Disk.this.ClusterSize;
      Disk.this.clusterArrayList = new ArrayList<>(Disk.this.clusterCount);
      Disk.this.add(new Directory("root"));
      return Disk.this;
    }
  }
}
