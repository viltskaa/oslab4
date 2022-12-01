module com.example.oslab {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.kordamp.bootstrapfx.core;
  requires org.jetbrains.annotations;

  opens com.example.oslab4 to javafx.fxml;

  exports com.example.oslab4.Controllers;
  exports com.example.oslab4.model;
  opens com.example.oslab4.Controllers to javafx.fxml;
  exports com.example.oslab4;
}