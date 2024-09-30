package com.example.bootlegllm;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloApplication extends Application {
    TextField textField;
    Label label;
    Button button;

    @Override
    public void start(Stage stage) throws IOException {
        FlowPane flowPane = new FlowPane();
        label = new Label("Gi URL: ");
        textField = new TextField();
        textField.setPrefColumnCount(30);
        label.setMinHeight(150);
        button = new Button("Submit");
        flowPane.getChildren().addAll(label, textField, button);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setAlignment(Pos.TOP_CENTER);
        button.setOnAction(e -> read(flowPane));

        Scene scene = new Scene(flowPane, Screen.getPrimary().getBounds().getWidth() * 0.7, Screen.getPrimary().getBounds().getHeight() * 0.7);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void read(FlowPane flow) {
        TextArea textArea = new TextArea();
        textArea.setMinHeight(Screen.getPrimary().getBounds().getHeight() * 0.6);
        textArea.setPrefWidth(Screen.getPrimary().getBounds().getWidth() * 0.6);
        textArea.setWrapText(true);

        try {
            String url = textField.getText();
            new URL(url);
            Document doc = Jsoup.connect(url).get();
            String text = doc.text();


            getData(text);

            flow.getChildren().clear();
            textArea.setText(text);

        } catch (IOException e) {
            if (e instanceof java.net.MalformedURLException) {
                textArea.setText("Error: Malformed URL. Please enter a valid URL.");
            } else {
                textArea.setText("Error fetching the URL. HTTP Status: " + e.getMessage());
            }
        } catch (Exception e) {
            textArea.setText("Unexpected error: " + e.getMessage());
        } finally {
            flow.getChildren().add(textArea);
        }

    }

    private static HashMap<String, Integer> getData(String text){
        try{
            //kode
            HashMap<String, HashMap<String, Integer>> map = new HashMap<>();


            String[] ord = text.replaceAll("[a-zA-Z,.?!\\s]", "").toLowerCase().split("\\s,.?!+");
            Pattern pattern;
            Matcher matcher;
            int[] antall = new int[5000];

            for (int i = 0; i<ord.length -2; i++) {
                map.computeIfAbsent(ord[i] + " " + ord[i+1], k -> new HashMap<>())
                                .merge(ord[i+2], 1, Integer::sum);
                pattern = Pattern.compile(ord[i] + " " + ord[i+1] + " " + ord[i + 2]);
                matcher = pattern.matcher(text);

                while(matcher.find()){
                    antall[i]++;
                }
                if(antall[i] > 1)
                    System.out.println("The expression: " + ord[i] + " " + ord[i+1] + " " + ord[i + 2] + " has been found " + antall[i] + " times");
                //j++;
            }

            return map;
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Fail");
        }
    }
}