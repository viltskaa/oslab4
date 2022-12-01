package com.example.oslab4.Controllers;

import com.example.oslab4.ModalsWindows.directoryAddWindow;
import com.example.oslab4.ModalsWindows.fileAddWindow;
import com.example.oslab4.ModalsWindows.pathEnterWindow;
import com.example.oslab4.model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class OsLabController {
  @FXML
  public Button Replace;
  @FXML
  public Button Delete;
  @FXML
  public Button Copy;
  @FXML
  public javafx.scene.canvas.Canvas Canvas;
  @FXML
  public Label pathLabel;
  @FXML
  private Button CreateDirectory;
  @FXML
  private Button CreateFile;
  @FXML
  private TreeView<Directory> FileTree;

  private fileAddController fileAdd;
  private directoryAddController directoryAdd;
  private pathEnterController pathEnter;
  private Disk disk;
  private final ArrayList<Cluster> selected = new ArrayList<>();
  private int state = 0;

  @FXML
  void initialize() {
    FileTree.setRoot(new TreeItem<>(new Directory("root")));
    FileTree.setEditable(false);

    CreateDirectory.setOnAction(event -> {
      try {
        ShowAddDirectoryDialog(event);
        AppendTree(directoryAdd.getDirectory());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    CreateFile.setOnAction(event -> {
      try {
        ShowAddFileDialog(event);
        AppendTree(fileAdd.getFile());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    Replace.setOnAction(event -> {
      try {
        setState(2);
        ShowPathEnterDialog(event);
        tryReplace(pathEnter.getPath());
        setState(0);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    Copy.setOnAction(event -> {
      try {
        setState(1);
        ShowPathEnterDialog(event);
        tryCopy(pathEnter.getPath());
        setState(0);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    Delete.setOnAction(event -> tryRemove());
    FileTree.getSelectionModel().selectedItemProperty().addListener(observable -> {
      pathLabel.setText("path: " + getPath(GetSelectedNode()));
      Draw();
    });

    disk = Disk.getBuilder()
                .setSize(16384)
                .setClusterSize(128)
                .build();
    disk.onUpdate(list -> Draw());
    Draw();
  }

  private void setState(int state) {
    this.state = state;
    Draw();
  }

  private void tryReplace(String path) {
    if (path == null)
      return;
    TreeItem<Directory> node = GetSelectedNode();
    if (node == null || node == FileTree.getRoot()) {
      return;
    }
    String localPath = getPath(node);
    if (disk.move(localPath, path)) {
      node.getParent().getChildren().remove(node);
      TreeItem<Directory> newRoot = getNodeByPath(path);
      newRoot.getChildren().add(node);
    }
  }

  private void tryCopy(String path) {
    if (path == null)
      return;
    TreeItem<Directory> node = GetSelectedNode();
    if (node == null || node == FileTree.getRoot()) {
      return;
    }
    String localPath = getPath(node);
    if (disk.copy(localPath, path)) {
      System.out.println("copy" +  " " + localPath + " " + path);
      TreeItem<Directory> newRoot = getNodeByPath(path);
      TreeItem<Directory> newNode = new TreeItem<>(node.getValue());
      newRoot.getChildren().add(newNode);
    }
  }

  private void tryRemove() {
    TreeItem<Directory> node = GetSelectedNode();
    if (node == null || node == FileTree.getRoot()) {
      return;
    }
    String path = getPath(node);
    if (disk.remove(path)) {
      node.getParent().getChildren().remove(node);
    }
  }

  private void Draw() {
    GraphicsContext graphicsContext = Canvas.getGraphicsContext2D();
    graphicsContext.clearRect(0, 0, Canvas.getWidth(), Canvas.getHeight());

    TreeItem<Directory> sel = GetSelectedNode();
    if (sel == null) {
      sel = FileTree.getRoot();
    }
    String path = getPath(sel);
    if (!path.isEmpty()) {
      selected.clear();
      Cluster node = disk.getClusterByPath(path);
      if (node != null)
        selected.addAll(
            disk.getAllClusters(node)
        );
    }
    int clusterCount = disk.getClusterCount();

    ClusterList cl = ClusterList.getList(clusterCount, disk.getClusters());

    int X = 0, Y = 0;
    for (Cluster cluster: cl) {
      if (cluster == null) {
        graphicsContext.setFill(Color.GRAY);
      }
      else if (selected.contains(cluster)) {
        switch (state) {
          case 0 -> graphicsContext.setFill(Color.BLUE);
          case 1 -> graphicsContext.setFill(Color.ORANGE);
          case 2 -> graphicsContext.setFill(Color.CORAL);
        }
      }
      else {
        graphicsContext.setFill(Color.GREEN);
      }
      int clusterWidth = 30;
      int clusterHeight = 30;
      graphicsContext.fillRect(X, Y, clusterWidth, clusterHeight);
      X += clusterWidth;
      if (X >= Canvas.getWidth()) {
        X = 0;
        Y += clusterHeight;
      }
    }
  }

  @Contract(pure = true)
  private @NotNull String RemoveRootFromPath(@NotNull String path) {
    return path.replace("root/", "");
  }

  private TreeItem<Directory> getNodeByPath(@NotNull String path) {
    return getNodeByPath(FileTree.getRoot(), path);
  }

  private TreeItem<Directory> getNodeByPath(@NotNull TreeItem<Directory> item, @NotNull String path) {
    path = RemoveRootFromPath(path);
    String localPath = path.contains("/") ? path.substring(0, path.indexOf("/")) : path;
    if (item.getValue().getName().equals(localPath)) {
      return item;
    }
    if (item.isExpanded()) {
      assert item.getChildren() != null;
      for (TreeItem<Directory> child : item.getChildren()) {
        if (child.getValue().getName().equals(localPath)) {
          return getNodeByPath(child, path.contains("/") ? path.replace(localPath + "/", "") : path);
        }
      }
    }
    return null;
  }

  private TreeItem<Directory> GetSelectedNode() {
    return FileTree.getSelectionModel().getSelectedItem();
  }

  private TreeItem<Directory> GetSelectedNodeForAdd() {
    TreeItem<Directory> node = FileTree.getSelectionModel().getSelectedItem();
    return node == null || !node.isExpanded() ? FileTree.getRoot() : node;
  }

  private TreeItem<Directory> wrapObjectInTreeItem(Directory object) {
    TreeItem<Directory> toReturn = new TreeItem<>(object);
    toReturn.setExpanded(!(object instanceof File));
    return toReturn;
  }

  private void ShowAddFileDialog(ActionEvent actionEvent) throws IOException {
    fileAdd = fileAddWindow.ModalStart(actionEvent);
  }

  private void ShowAddDirectoryDialog(ActionEvent actionEvent) throws IOException {
    directoryAdd = directoryAddWindow.ModalStart(actionEvent);
  }

  private void ShowPathEnterDialog(ActionEvent actionEvent) throws IOException {
    pathEnter = pathEnterWindow.ModalStart(actionEvent);
  }

  private boolean HaveDuplicate(@NotNull Directory object) {
    ObservableList<TreeItem<Directory>> children = GetSelectedNodeForAdd().getChildren();
    for (TreeItem<Directory> child: children) {
      if (child.getValue().equals(object))
        return true;
    }
    return false;
  }

  private @NotNull String getPath(@NotNull TreeItem<Directory> root) {
    TreeItem<Directory> selected = root;
    StringBuilder path = new StringBuilder();
    while (selected != null) {
      path.insert(0, " " + selected.getValue().getName());
      selected = selected.getParent();
    }
    return path.toString().trim().replace(" ", "/");
  }

  private void AppendTree(Directory object) {
    if (object == null || HaveDuplicate(object)) return;
    TreeItem<Directory> node = wrapObjectInTreeItem(object);
    TreeItem<Directory> root = GetSelectedNodeForAdd();
    if (disk.add(object, getPath(root))) {
      root.getChildren().add(node);
      FileTree.getSelectionModel().select(node);
    }
  }
}
