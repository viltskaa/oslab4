package com.example.oslab4.ModalsWindows;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class modalWindow {
  public static Stage ConfigStage(Parent root) {
    Stage stage = new Stage();
    stage.initStyle(StageStyle.UNDECORATED);
    stage.setScene(new Scene(root));
    stage.initModality(Modality.WINDOW_MODAL);
    stage.setResizable(false);
    return stage;
  }
}
