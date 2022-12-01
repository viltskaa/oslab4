package com.example.oslab4.Controllers;

import com.example.oslab4.model.Directory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class directoryAddController {
  @FXML
  private TextField fileName;

  private Directory directory;

  @FXML
  void initialize() {}

  private boolean CheckValues() {
    return !fileName.getText().trim().isEmpty();
  }

  @FXML
  private void OnClose(@NotNull ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.hide();
  }

  @FXML
  private void OnCreate(ActionEvent actionEvent) {
    if (CheckValues()) {
      directory = new Directory(fileName.getText());
      OnClose(actionEvent);
    }
  }

  public Directory getDirectory() {
    return directory;
  }
}
