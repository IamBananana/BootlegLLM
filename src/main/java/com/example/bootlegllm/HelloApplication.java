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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


// Personer på gruppen: Erik, Richard, Simen og Ulrikke
// Kilder koden er trent på: grunnet startordene "det + var" har vi tatt utganspunkt i snl andre verdenskrig
public class HelloApplication extends Application {
    TextField textField;
    TextField filnavn;
    Label label;
    Label filInfo;
    Button btnUrl, btnGenerate, btnSave;
    static TextArea textArea;
    StringBuilder txtBuilder;
    static String[] ord;

    /**
     * Oppretter GUI
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FlowPane flowPane = new FlowPane();

        label = new Label("Gi URL: ");
        textField = new TextField();
        textField.setPrefColumnCount(30);
        filInfo = new Label();

        textArea = new TextArea();
        textArea.setMinHeight(Screen.getPrimary().getBounds().getHeight() * 0.6);
        textArea.setPrefWidth(Screen.getPrimary().getBounds().getWidth() * 0.6);
        textArea.setWrapText(true);

        label.setMinHeight(150);
        btnUrl = new Button("Add URL");
        btnGenerate = new Button("Generate");
        btnSave = new Button("Save text to file");
        filnavn = new TextField("Angi filnavn:");

        flowPane.getChildren().addAll(label, textField, btnUrl, btnGenerate, filnavn, btnSave, filInfo, textArea);
        flowPane.setVgap(10);
        flowPane.setHgap(10);
        flowPane.setAlignment(Pos.TOP_CENTER);

        // Angir startordene manuelt her
        txtBuilder = new StringBuilder();
        String ord1 = "det";
        String ord2 = "var";

        btnUrl.setOnAction(e -> read(flowPane));
        btnGenerate.setOnAction(e -> generateText(getData(txtBuilder.toString()), ord1, ord2));
        btnSave.setOnAction(e -> {
            try {
                saveToFile();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });


        Scene scene = new Scene(flowPane, Screen.getPrimary().getBounds().getWidth() * 0.7, Screen.getPrimary().getBounds().getHeight() * 0.7);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Metode for å lese tekst innholdet på nettside. Bruker JSoup for å lese teksten
     * @param flow
     */
    private void read(FlowPane flow) {

        try {
            String url = textField.getText();
            new URL(url);
            Document doc = Jsoup.connect(url).get();
            txtBuilder.append(doc.text());
            textArea.setText(textArea.getText() + "Successfully added URL: " + url + "\n");
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

    /**Erik Runde
     * Metoden for å konvertere teksten til ord, basert på whitespace og ulike tegn.
     * Tar inn de to første ordene som nøkkel, også tar inn siste ordet som innermap
     * @param text
     * @return map - skriver ut hele mapen
     * try-catch kan være nyttig hvis det er noe feil med regex-en eller null tekst
     */
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

    /**
     * Generer den tilfeldige teksten.
     * @param data Tar inn HashMap som inneholder all data om antall forekomster av ulike ord kombinasjoner.
     * @param startOrd1
     * @param startOrd2 Bruker startOrd1/2 som en statisk to ords kombinasjon og bruker dette til å generere det første tilfeldig 3. ord.
     *                  <p>
     *                  startOrd1/2 blir satt som startKey, men når while loopen starter så blir startKey satt til startOrd2 og pickedWord (tilfeldig generert ord basert på startKey)
     *                  startKey blir satt til startOrd2 + pickedWord så blir startOrd2 satt til pickedWord og deretter blir pickedWord nytt tileldig ord basert på key.
     *                  Gjennom hver iterasjon blir det tilfeldige ordet lagret i en out String. Whilen er hard kodet til å iterere 1000 ganger (dvs. vi får 1000 + 2 ord)
     *                  Metoden returnerer ingenting, men sender out String til en textArea som vises til brukeren når while-loopen er ferdig.
     */
    private static void generateText(HashMap<String, HashMap<String, Integer>> data, String startOrd1, String startOrd2) {
        StringBuilder out = new StringBuilder();
        String startKey = startOrd1 + " " + startOrd2;

        if (!data.containsKey(startKey)) {
            textArea.setText("Kombinasjonen \"" + startKey + "\" finnes ikke i data." + "\n");
            return;
        }

        String pickedWord = wordPicker(data.get(startKey));
        out.append(startOrd1).append(" ").append(startOrd2).append(" ").append(pickedWord).append(" ");

        int counter = 0;
        while (data.containsKey(startKey) && counter < 1000) {

            startKey = startOrd2 + " " + pickedWord;

            startOrd2 = pickedWord;

            pickedWord = wordPicker(data.get(startKey));
            out.append(pickedWord).append(" ");

            counter++;
        }

        textArea.setText(out.toString());
    }

    /**
     * @param innerMap Tar innerste mappet siden det er der sannsynligheten av ord ligger
     *                 Looper gjennom hele mappet og summerer opp antall forekomster slik at
     *                 man kan lage vektet sannsynlighet basert på frekvens av ord
     *                 <p>
     *                 Random velger random int basert på summen av frekvensen og randomValue blir oppdatert etter hver
     *                 loop med vekten vi skal sjekke på. Når randomValue - vekt er mindre enn 0 har vi funnet
     *                 ordet
     * @return Returnerer feil hvis loopen har gått gjennom og ikke finnet et ord
     */
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
                return word;
            }
        }
        return "$$$ Feil ved trekning av ord!!!";
    }

    /**
     * Metode for å lagre den genererte teksten i textarea over til et filnavn som bruker selv velger.
     * Dersom fil lagringen er vellykket, kommer det beskjed om dette i en label, samme gjelder dersom det ikke går.
     * @throws FileNotFoundException
     */
    public void saveToFile() throws FileNotFoundException {
        try {
            String fil = filnavn.getText();
            PrintWriter skriver = new PrintWriter(fil + ".txt");
            skriver.println(textArea.getText());
            skriver.close();
            filInfo.setText("Filen ble lagret som: " + fil + ".txt");
        } catch (FileNotFoundException e) {
            filInfo.setText(e.getMessage());
        }
    }
}