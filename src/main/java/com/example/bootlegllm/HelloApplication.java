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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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


            HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
            map = getData(text);
            System.out.println(generateTextUlrikkeVersion(map, "kom den"));
            wordPicker(map);

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

    private static HashMap<String, HashMap<String, Integer>> getData(String text) {
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

        try {
            //String[] ord = text.replaceAll("[a-zA-Z,.?!\\s]", "").toLowerCase().split("\\s,.?!+");
            //Regex funker ikke helt enda
            String[] ord = text.replaceAll("[^a-zA-ZæøåÆØÅ ]", "").toLowerCase().split("[ ,;.!:?]+");

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

            HashMap<String, Integer> innerMap = data.get(key);
            int sum = 0;

            //Not finished...

            for (String innerKey : innerMap.keySet()) {
                sum += innerMap.get(innerKey);
            }

            out.append(calculateWord(sum) + " ");
        }

        return out.toString();
    }

    //Add more parameters?
    private static String calculateWord(int sum) {
        String lastWord = "";

        //Make calculated word here

        return lastWord;
    }

    //  ikke ferdig, let me cook.
    //relativt cooked nå
    private static void wordPicker(HashMap<String, HashMap<String, Integer>> data) {
        int sum = 0;
        for (Map.Entry<String, HashMap<String, Integer>> entry : data.entrySet()) {
            HashMap<String, Integer> innerMap = entry.getValue();
            for (Integer value : innerMap.values()) {
                sum += value;
            }
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * sum;

        for (Map.Entry<String, HashMap<String, Integer>> entry : data.entrySet()) {
            HashMap<String, Integer> innerMap = entry.getValue();
            for (Map.Entry<String, Integer> innerEntry : innerMap.entrySet()) {
                String word = innerEntry.getKey();
                int weight = innerEntry.getValue();
                randomValue -= weight;

                if (randomValue <= 0) {
                    System.out.println("Selected word: " + word);
                    return;
                }
            }
        }
    }

    private static String generateTextUlrikkeVersion(HashMap<String, HashMap<String, Integer>> data, String startWords) {
        StringBuilder output = new StringBuilder(startWords);
        String[] words = startWords.split(" ");

        for (int i = 0; i < 50; i++) {
            String key = words[words.length - 2] + " " + words[words.length - 1];
            if (!data.containsKey(key)) {
                break;
            }

            String nextWord = chooseNextWordUlrikkeVersion(data.get(key));
            output.append(" ").append(nextWord);
            words = new String[] {words[words.length - 1], nextWord};
        }

        return output.toString();
    }
    private static String chooseNextWordUlrikkeVersion(HashMap<String, Integer> wordMap) {
        int total = wordMap.values().stream().mapToInt(Integer::intValue).sum();
        double randomValue = new Random().nextDouble() * total;

        for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
            randomValue -= entry.getValue();
            if (randomValue <= 0) {
                return entry.getKey();
            }
        }
        return "";
    }
}