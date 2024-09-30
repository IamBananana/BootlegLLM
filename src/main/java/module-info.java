module com.example.bootlegllm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bootlegllm to javafx.fxml;
    exports com.example.bootlegllm;
}