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
    Button btnUrl, btnGenerate;
    static TextArea textArea;
    StringBuilder txtBuilder;
    static String[] ord;

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
        String ord1 = "det";
        String ord2 = "var";

        btnUrl.setOnAction(e -> read(flowPane));
        btnGenerate.setOnAction(e -> generateText(getData(txtBuilder.toString()), ord1, ord2));

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
            textArea.setText( textArea.getText() +"Successfully added URL: " + url + "\n");
        } catch (IOException e) {
            if (e instanceof java.net.MalformedURLException) {
                textArea.setText("Error: Malformed URL. Please enter a valid URL." + "\n");
            } else {
                textArea.setText("Error fetching the URL. HTTP Status: " + e.getMessage() + "\n");
            }
        } catch (Exception e) {
            textArea.setText("Unexpected error: " + e.getMessage() + "\n");
        }
    }


    private static HashMap<String, HashMap<String, Integer>> getData(String text) {
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

        try {
            ord = text.replaceAll("[^a-zA-ZæøåÆØÅ,.;:_!?/+\\- ]", "")
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

    private static void generateText(HashMap<String, HashMap<String, Integer>> data, String startOrd1, String startOrd2) {
        StringBuilder out = new StringBuilder();
        String startKey = startOrd1 + " " + startOrd2;

        if (!data.containsKey(startKey)) {
            textArea.setText("Kombinasjonen \"" + startKey + "\" finnes ikke i data." + "\n");
            return;
        }

        String nesteOrd = wordPicker(data.get(startKey));
        out.append(startOrd1).append(" ").append(startOrd2).append(" ").append(nesteOrd).append(" ");
        String nøkkel = startOrd2 + " " + nesteOrd;

        for (String key : data.keySet()) {
            String[] kombo = key.split(" ");
            if (kombo.length < 2) {
                continue;
            }

            String lastWord = kombo[1];
            String next = wordPicker(data.get(nøkkel));
            nøkkel = lastWord + " " + next;
            out.append(nøkkel).append(" ");
            }
        textArea.setText(out.toString());
    }

    private static void generateText2(HashMap<String, HashMap<String, Integer>> data, String startOrd1, String startOrd2) {
        StringBuilder out = new StringBuilder();
        boolean hasMoreWords = true;
        String startKey = startOrd1 + " " + startOrd2;

        if (!data.containsKey(startKey)) {
            textArea.setText("Kombinasjonen \"" + startKey + "\" finnes ikke i data." + "\n");
            return;
        }

        String pickedWord = wordPicker(data.get(startKey));
        out.append(startOrd1).append(" ").append(startOrd2).append(" ").append(pickedWord).append(" ");
       
        /*
        while (hasMoreWords) {
            startOrd1 = startOrd2;
            startOrd2 = pickedWord;
            startKey = startOrd1 + " " + startOrd2;

            if (data.containsKey(startKey)) {
                pickedWord = wordPicker(data.get(startKey));
                if (pickedWord == null || pickedWord.isEmpty()) {
                    hasMoreWords = false;
                } else {
                    out.append(pickedWord).append(" ");
                }
            } else {
                hasMoreWords = false;
            }
        }
        */

        //Ny
        int counter = 0;
        while(data.containsKey(startKey) && counter < 1000){

            startKey = startOrd2 + " " + pickedWord;

            startOrd2 = pickedWord;

            pickedWord = wordPicker(data.get(startKey));
            out.append(pickedWord).append(" ");
            System.out.println(counter);

            counter++;
        }
        
        textArea.setText(out.toString());
    }

    private static String wordPicker(HashMap<String, Integer> innerMap) {
        if (innerMap == null || innerMap.isEmpty()) {
            return "";
        }

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
                //Bruh...
                if (word != null && !word.isEmpty() && word.charAt(0) == ',') {
                    return word;
                } else {
                    return word;
                }
            }
        }
        return "$$$ Feil ved trekning av ord!!!";
    }

}
