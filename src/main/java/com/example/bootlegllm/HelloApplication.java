package com.example.bootlegllm;

import com.sun.source.tree.Tree;
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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HelloApplication extends Application {
    TextField textField;
    Label label;
    Button btnUrl, btnGenerate;
    static TextArea textArea;
    StringBuilder txtBuilder;

    @Override
    public void start(Stage stage) {
        FlowPane flowPane = new FlowPane();

        label = new Label("Gi URL: ");
        textField = new TextField();
        textField.setPrefColumnCount(30);

        textArea = new TextArea();
        textArea.setMinHeight(Screen.getPrimary().getBounds().getHeight() * 0.6);
        textArea.setPrefWidth(Screen.getPrimary().getBounds().getWidth() * 0.6);
        textArea.setWrapText(true);

        label.setMinHeight(150);
        btnUrl = new Button("Add URL");
        btnGenerate = new Button("Generate");

        flowPane.getChildren().addAll(label, textField, btnUrl, btnGenerate, textArea);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setAlignment(Pos.TOP_CENTER);

        txtBuilder = new StringBuilder();
        btnUrl.setOnAction(e -> read(flowPane));
        btnGenerate.setOnAction(e -> generateText(getData(txtBuilder.toString())));

        Scene scene = new Scene(flowPane, Screen.getPrimary().getBounds().getWidth() * 0.7, Screen.getPrimary().getBounds().getHeight() * 0.7);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void read(FlowPane flow) {

        try {
            String url = textField.getText();
            new URL(url);
            Document doc = Jsoup.connect(url).get();
            txtBuilder.append(doc.text());

        } catch (IOException e) {
            if (e instanceof java.net.MalformedURLException) {
                textArea.setText("Error: Malformed URL. Please enter a valid URL.");
            } else {
                textArea.setText("Error fetching the URL. HTTP Status: " + e.getMessage());
            }
        } catch (Exception e) {
            textArea.setText("Unexpected error: " + e.getMessage());
        }
    }


    private static HashMap<String, HashMap<String, Integer>> getData(String text) {
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

        try {
            //Regex funker ikke helt enda, hvordan beholde ,.;:-_!?/+ ??????. Esså sykt done med regex fakk d her
            //funker sånn isj men omg
            String[] ord = text.replaceAll("[^a-zA-ZæøåÆØÅ,.;:_!?/+\\- ]", "")
                            .split("(?=[,;.!:?+/_-])|(?<=[,;.!:?+/_-])|\\s+");

            for (int i = 0; i < ord.length - 2; i++) {
                map.computeIfAbsent(ord[i] + " " + ord[i + 1], k -> new HashMap<>()).merge(ord[i + 2], 1, Integer::sum);
            }

            System.out.println("Map:" + map);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fail");
        }
        return map;
    }

    private static String generateText(HashMap<String, HashMap<String, Integer>> data) {
        StringBuilder out = new StringBuilder();
        Set<String> keySet = data.keySet();

        for (String key : keySet) {
            out.append(key);

            //Sends in innerMap
            out.append(wordPicker(data.get(key)));
        }
        return out.toString();
    }

    //  ikke ferdig, let me cook.
    //relativt cooked nå
    private static String wordPicker(HashMap<String, Integer> innerMap) {
        Random random = new Random();

        int sum = 0;
        for (Integer value : innerMap.values()) {
            sum += value;
        }

        int randomValue = random.nextInt(sum);

        for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
            String word = innerEntry.getKey();
            int weight = innerEntry.getValue();
            randomValue -= weight;

            if (randomValue < 0) {
                return word;
            }
        }
        return "$$$ Feil ved trekning av ord!!!";
    }
}