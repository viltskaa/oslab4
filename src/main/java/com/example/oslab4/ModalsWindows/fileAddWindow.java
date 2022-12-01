package com.example.oslab4.ModalsWindows;

import com.example.oslab4.Controllers.fileAddController;
import com.example.oslab4.OsLab;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class fileAddWindow {
  public static fileAddController ModalStart(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(OsLab.class.getResource("fileAdd-view.fxml"));

    Stage stage = modalWindow.ConfigStage(fxmlLoader.load());
    stage.initOwner(((Node)event.getSource()).getScene().getWindow());
    stage.showAndWait();

    return fxmlLoader.getController();
  }
}
