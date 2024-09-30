module com.example.bootlegllm {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;


    opens com.example.bootlegllm to javafx.fxml;
    exports com.example.bootlegllm;
}