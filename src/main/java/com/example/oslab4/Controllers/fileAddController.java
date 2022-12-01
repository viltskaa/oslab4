package com.example.oslab4.Controllers;

import com.example.oslab4.model.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class fileAddController {
  @FXML
  private TextField fileName;

  @FXML
  private TextField fileSize;

  private File file;

  @FXML
  void initialize() {}

  private boolean CheckValues() {
    try {
      Integer.parseInt(fileSize.getText());
    }
    catch (NumberFormatException e) {
      return false;
    }
    return !fileName.getText().trim().isEmpty();
  }

  @FXML
  private void OnClose(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.hide();
  }

  @FXML
  private void OnCreate(ActionEvent actionEvent) {
    if (CheckValues()) {
      file = new File(Integer.parseInt(fileSize.getText()), fileName.getText());
      OnClose(actionEvent);
    }
  }

  public File getFile() {
    return file;
  }
}
