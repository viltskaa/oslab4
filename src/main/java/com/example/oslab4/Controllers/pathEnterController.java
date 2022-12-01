package com.example.oslab4.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class pathEnterController {

  @FXML
  private Button Cancel;

  @FXML
  private Button Create;

  @FXML
  private TextField fileName;

  private String path;

  private boolean checkValues() {
    return !fileName.getText().trim().isEmpty();
  }

  @FXML
  void OnClose(@NotNull ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.hide();
  }

  @FXML
  void OnCreate(ActionEvent event) {
    if (checkValues()) {
      path = fileName.getText().trim();
      OnClose(event);
    }
  }

  public String getPath() {
    return path;
  }
}
