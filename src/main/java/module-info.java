module com.example.poddavki_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.poddavki_project to javafx.fxml;
    exports com.example.poddavki_project;
}