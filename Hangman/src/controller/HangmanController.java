package controller;

import apptemplate.AppTemplate;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import components.AppDataComponent;
import components.AppFileComponent;
import data.GameData;
import gui.Workspace;
import hangman.Hangman;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import propertymanager.PropertyManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ui.AppGUI;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Set;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_WORKDIR_PATH;

/**
 * @author Ritwik Banerjee
 */
public class HangmanController implements FileController {

    private AppTemplate appTemplate; // shared reference to the application
    private GameData    gamedata;    // shared reference to the game being played, loaded or saved
    private Text[]      progress;    // reference to the text area for the word
    private Text[]      guessV1;
    private Text[]      guessV2;
    private Text[]      guessV3;
    private Text[]      guessV4;
    private Text[]      guessV5;
    private Text[]      guessV6;
    private boolean     success;     // whether or not player was successful
    private int         discovered;  // the number of letters already discovered
    private Button      gameButton;  // shared reference to the "start game" button
    private Label       remains;     // dynamically updated label that indicates the number of remaining guesses
    private boolean     gameover;    // whether or not the current game is already over
    private boolean     savable;
    private Path        workFile;
    private File        currentWorkFile;
    private Canvas      c = new Canvas(300, 600);
    private Button      hint;
    private Canvas      c1 = new Canvas(300, 360);

    public HangmanController(AppTemplate appTemplate, Button gameButton) {
        this(appTemplate);
        this.gameButton = gameButton;
    }

    public HangmanController(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
    }

    public void enableGameButton() {
        if (gameButton == null) {
            Workspace workspace = (Workspace) appTemplate.getWorkspaceComponent();
            gameButton = workspace.getStartGame();
        }
        gameButton.setDisable(false);
    }

    public void setGamedata (AppTemplate app) {
        gamedata = new GameData(app);
    }

    public void setGamedata (String tWord, int rGuess, Set<Character> gGuess, Set<Character> bGuess) {
        gamedata.setTargetWord(tWord);
        gamedata.setRemainingGuesses(rGuess);
        gamedata.setGoodGuesses(gGuess);
        gamedata.setBadGuesses(bGuess);
    }

    public GameData getGamedata () {

        return gamedata;
    }

    public void start() {
        setGamedata(appTemplate);
        c = new Canvas(300, 600);
        gameover = false;
        success = false;
        savable = true;
        discovered = 0;
        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
        appTemplate.getGUI().updateWorkspaceToolbar(savable);
        HBox remainingGuessBox = gameWorkspace.getRemainingGuessBox();
        HBox stickMan = gameWorkspace.getStickMan();
        initCanvas(stickMan);
        HBox guessedLetters    = (HBox) gameWorkspace.getGameTextsPane().getChildren().get(1);

        remains = new Label(Integer.toString(GameData.TOTAL_NUMBER_OF_GUESSES_ALLOWED));
        remainingGuessBox.getChildren().addAll(new Label("Remaining Guesses: "), remains);
        initWordGraphics(guessedLetters);
        initGuessedWords(guessedLetters);
        if (is7More(gamedata.getTargetWord())) {
            hint = new Button();
            hint.setText("HINT");
            hint.setOnAction(e -> {
                String tS = "";
                char tC = ' ';
                    Random r = new Random();
                    int n = r.nextInt(gamedata.getTargetWord().length() - discovered);
                    for (int i = 0; i < gamedata.getTargetWord().length(); i++) {
                        if (!gamedata.getGoodGuesses().contains(gamedata.getTargetWord().charAt(i))) {
                            tS = tS + gamedata.getTargetWord().charAt(i);
                        }
                    }
                    tC = tS.charAt(n);

                    for (int i = 0; i < ((progress.length) / 3); i++) {
                        boolean k = true;
                        if (gamedata.getTargetWord().charAt(i) == Character.toLowerCase(tC)) {
                            progress[(3 * i) + 1].setVisible(true);
                            discovered++;
                            if (k) {
                                gamedata.addGoodGuess(tC);
                            }
                            k = false;
                        }
                    }
                    gamedata.setRemainingGuesses(gamedata.getRemainingGuesses() - 1);
                    updateCanvas(gamedata);
                    success = (discovered == gamedata.getTargetWord().length());
                    remains.setText(Integer.toString(gamedata.getRemainingGuesses()));
                    savable = true;
                    AppGUI.updateToolbarControls(!savable);
                    tC = Character.toUpperCase(tC);
                    int temp = tC - 65;
                if (temp < 5) {
                    if (tC >= 65 && tC <= 90) {
                        guessV1[(int) tC - 65].setStroke(Color.WHITE);
                    }
                    if (tC >= 97 && tC <= 122) {
                        guessV1[(int) tC - 97].setStroke(Color.WHITE);
                    }
                }
                else if (temp - 5 < 5) {
                    if (tC >= 65 && tC <= 90) {
                        guessV2[(int) tC - 5 - 65].setStroke(Color.WHITE);
                    }
                    if (tC >= 97 && tC <= 122) {
                        guessV2[(int) tC - 5 - 97].setStroke(Color.WHITE);
                    }
                }
                else if (temp - 10 < 5) {
                    if (tC >= 65 && tC <= 90) {
                        guessV3[(int) tC - 10 - 65].setStroke(Color.WHITE);
                    }
                    if (tC >= 97 && tC <= 122) {
                        guessV3[(int) tC - 10 - 97].setStroke(Color.WHITE);
                    }
                }
                else if (temp - 15 < 5) {
                    if (tC >= 65 && tC <= 90) {
                        guessV4[(int) tC - 15 - 65].setStroke(Color.WHITE);
                    }
                    if (tC >= 97 && tC <= 122) {
                        guessV4[(int) tC - 15 - 97].setStroke(Color.WHITE);
                    }
                }
                else if (temp - 20 < 5) {
                    if (tC >= 65 && tC <= 90) {
                        guessV5[(int) tC - 20 -65].setStroke(Color.WHITE);
                    }
                    if (tC >= 97 && tC <= 122) {
                        guessV5[(int) tC - 20 - 97].setStroke(Color.WHITE);
                    }
                }
                else if (temp - 25 < 5) {
                    if (tC >= 65 && tC <= 90) {
                        guessV6[(int) tC - 25 - 65].setStroke(Color.WHITE);
                    }
                    if (tC >= 97 && tC <= 122) {
                        guessV6[(int) tC - 25 - 97].setStroke(Color.WHITE);
                    }
                }
                    hint.setDisable(true);
            });
            stickMan.getChildren().add(hint);
        }
        play();
    }

    private boolean is7More (String s) {
        boolean b = false;
        int unique = 0;
        String tempC = "";
        for (int i = 0; i < gamedata.getTargetWord().length(); i++) {
            boolean c = false;
            for (int j = 0; j < tempC.length(); j++) {
                if (tempC.charAt(j) == gamedata.getTargetWord().charAt(i)) {
                    c = true;
                }
            }
            if (!c) {
                tempC = tempC + gamedata.getTargetWord().charAt(i);
            }
        }
        unique = tempC.length();
        if (unique > 7) {
            return true;
        }
        else {
            return false;
        }
    }

    private void end() {
        if (success) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show1("Winner!", "You Won!");

        }
        else {
            for(int i = 0; i < progress.length; i++) {
                if (!progress[i].isVisible()) {
                    progress[i].setStroke(Color.RED);
                    progress[i].setVisible(true);
                }
            }
        }
        appTemplate.getGUI().getPrimaryScene().setOnKeyTyped(null);
        gameover = true;
        gameButton.setDisable(true);
        savable = false; // cannot save a game that is already over
        appTemplate.getGUI().updateWorkspaceToolbar(savable);

    }

    private void initWordGraphics(HBox guessedLetters) {
        StackPane sp = new StackPane();
        HBox tHB = new HBox();
        char[] targetword = gamedata.getTargetWord().toCharArray();
        progress = new Text[targetword.length * 3];
        for (int i = 0; i < progress.length; i++) {
            progress[i] = new Text("  ");
            progress[i].setFont(Font.font("Monospaced", 20));
            i++;
            progress[i] = new Text(Character.toString(targetword[(i - 1) / 3]));
            progress[i].setVisible(false);
            progress[i].setFont(Font.font("Monospaced", 20));
            i++;
            progress[i] = new Text("  ");
            progress[i].setFont(Font.font("Monospaced", 20));
        }
        Canvas tC = new Canvas(21 * progress.length, 40);
        GraphicsContext gc = tC.getGraphicsContext2D();
        gc.setFill(Color.TEAL);
        gc.fillRect(0, 0, 21 * progress.length, 40);
        gc.setFill(Color.PINK);
        for (int i = 0; i < progress.length/3; i++) {
            gc.fillRect((63*i) + 2, 2, 59, 36);
        }
        tHB.getChildren().addAll(progress);
        sp.getChildren().addAll(tC);
        sp.getChildren().addAll(tHB);
        sp.setAlignment(Pos.TOP_RIGHT);
        StackPane.setMargin(tHB, new Insets(10, 0, 10, 70));
        guessedLetters.getChildren().addAll(sp);
    }

    private void initGuessedWords (HBox guessedLetters) {
        GraphicsContext gc = c1.getGraphicsContext2D();
        gc.setFill(Color.GOLD);
        gc.fillRect(0, 0, 300, 300);
        gc.fillRect(0, 0, 60, 360);
        StackPane sp = new StackPane();
        VBox vB = new VBox(40);
        HBox hB;
        guessV1 = new Text[5];
        int j = 0;
        for (int i = 65; i < 70; i++) {
            guessV1[j] = new Text("  " + Character.toString((char) i) + "  ");
            guessV1[j].setFont(Font.font("Monospaced", 20));
            //guessV[j].setVisible(false);
            j++;
        }
        hB = new HBox();
        hB.getChildren().addAll(guessV1);
        vB.getChildren().addAll(hB);

        guessV2 = new Text[5];
        j = 0;
        for (int i = 70; i < 75; i++) {
            guessV2[j] = new Text("  " + Character.toString((char) i) + "  ");
            guessV2[j].setFont(Font.font("Monospaced", 20));
            //guessV[j].setVisible(false);
            j++;
        }
        hB = new HBox();
        hB.getChildren().addAll(guessV2);
        vB.getChildren().addAll(hB);
        guessV3 = new Text[5];
        j = 0;
        for (int i = 75; i < 80; i++) {
            guessV3[j] = new Text("  " + Character.toString((char) i) + "  ");
            guessV3[j].setFont(Font.font("Monospaced", 20));
            //guessV[j].setVisible(false);
            j++;
        }
        hB = new HBox();
        hB.getChildren().addAll(guessV3);
        vB.getChildren().addAll(hB);
        guessV4 = new Text[5];
        j = 0;
        for (int i = 80; i < 85; i++) {
            guessV4[j] = new Text("  " + Character.toString((char) i) + "  ");
            guessV4[j].setFont(Font.font("Monospaced", 20));
            //guessV[j].setVisible(false);
            j++;
        }
        hB = new HBox();
        hB.getChildren().addAll(guessV4);
        vB.getChildren().addAll(hB);
        guessV5 = new Text[5];
        j = 0;
        for (int i = 85; i < 90; i++) {
            guessV5[j] = new Text("  " + Character.toString((char) i) + "  ");
            guessV5[j].setFont(Font.font("Monospaced", 20));
            //guessV[j].setVisible(false);
            j++;
        }
        hB = new HBox();
        hB.getChildren().addAll(guessV5);
        vB.getChildren().addAll(hB);

        guessV6 = new Text[1];
        guessV6[0] = new Text("  " + "Z" + "  ");
        guessV6[0].setFont(Font.font("Monospaced", 20));
        //guessV[0].setVisible(false);
        hB = new HBox();
        hB.getChildren().addAll(guessV6);
        vB.getChildren().addAll(hB);

        sp.getChildren().addAll(c1);
        sp.getChildren().addAll(vB);
        sp.setAlignment(Pos.TOP_LEFT);

        guessedLetters.getChildren().addAll(sp);
    }

    private void initCanvas (HBox stickMan){
        stickMan.getChildren().add(c);
    }

    private void updateCanvas (GameData data) {
        switch (data.getRemainingGuesses()) {
            case 9:
                GraphicsContext gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                break;
            case 8:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                break;
            case 7:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                break;
            case 6:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                break;
            case 5:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                gc.fillOval(100, 90, 50, 50);
                break;
            case 4:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                gc.fillOval(100, 90, 50, 50);
                gc.strokeLine(125, 115, 125, 220);
                break;
            case 3:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                gc.fillOval(100, 90, 50, 50);
                gc.strokeLine(125, 115, 125, 220);
                gc.strokeLine(125, 160, 90, 200);
                break;
            case 2:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                gc.fillOval(100, 90, 50, 50);
                gc.strokeLine(125, 115, 125, 220);
                gc.strokeLine(125, 160, 90, 200);
                gc.strokeLine(125, 160, 160, 200);
                break;
            case 1:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                gc.fillOval(100, 90, 50, 50);
                gc.strokeLine(125, 115, 125, 220);
                gc.strokeLine(125, 160, 90, 200);
                gc.strokeLine(125, 160, 160, 200);
                gc.strokeLine(125, 220, 90, 230);
                break;
            case 0:
                gc = c.getGraphicsContext2D();
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                gc.strokeLine(10, 270, 180, 270);
                gc.strokeLine(10, 270, 10, 10);
                gc.strokeLine(10, 10, 125, 10);
                gc.strokeLine(125, 10, 125, 90);
                gc.fillOval(100, 90, 50, 50);
                gc.strokeLine(125, 115, 125, 220);
                gc.strokeLine(125, 160, 90, 200);
                gc.strokeLine(125, 160, 160, 200);
                gc.strokeLine(125, 220, 90, 230);
                gc.strokeLine(125, 220, 160, 230);
                break;
            default:
                break;
        }
    }

    public void play() {
        savable = true;
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                appTemplate.getGUI().getPrimaryScene().setOnKeyTyped((KeyEvent event) -> {
                    char guess = event.getCharacter().charAt(0);
                    if (Character.toLowerCase(guess) >= 97 && Character.toLowerCase(guess) <=122) {
                        if (!alreadyGuessed(guess)) {
                            boolean goodguess = false;
                            for (int i = 0; i < ((progress.length) / 3); i++) {
                                boolean k = true;
                                if (gamedata.getTargetWord().charAt(i) == Character.toLowerCase(guess)) {
                                    progress[(3 * i) + 1].setVisible(true);
                                    discovered++;
                                    if (k) {
                                        gamedata.addGoodGuess(guess);
                                        goodguess = true;
                                    }
                                    k = false;
                                }
                            }
                            if (!goodguess)
                                gamedata.addBadGuess(guess);

                            updateCanvas(gamedata);
                            success = (discovered == gamedata.getTargetWord().length());
                            remains.setText(Integer.toString(gamedata.getRemainingGuesses()));
                            if (gamedata.getRemainingGuesses() == 1 && is7More(gamedata.getTargetWord())) {
                                hint.setDisable(true);
                            }
                            savable = true;
                            AppGUI.updateToolbarControls(!savable);
                            guess = Character.toUpperCase(guess);
                            int temp = guess - 65;
                            if (temp < 5) {
                                if (guess >= 65 && guess <= 90) {
                                    guessV1[(int) guess - 65].setStroke(Color.WHITE);
                                }
                                if (guess >= 97 && guess <= 122) {
                                    guessV1[(int) guess - 97].setStroke(Color.WHITE);
                                }
                            }
                            else if (temp - 5 < 5) {
                                if (guess >= 65 && guess <= 90) {
                                    guessV2[(int) guess- 5 - 65].setStroke(Color.WHITE);
                                }
                                if (guess >= 97 && guess <= 122) {
                                    guessV2[(int) guess - 5 - 97].setStroke(Color.WHITE);
                                }
                            }
                            else if (temp - 10 < 5) {
                                if (guess >= 65 && guess <= 90) {
                                    guessV3[(int) guess - 10 - 65].setStroke(Color.WHITE);
                                }
                                if (guess >= 97 && guess <= 122) {
                                    guessV3[(int) guess - 10 - 97].setStroke(Color.WHITE);
                                }
                            }
                            else if (temp - 15 < 5) {
                                if (guess >= 65 && guess <= 90) {
                                    guessV4[(int) guess - 15 - 65].setStroke(Color.WHITE);
                                }
                                if (guess >= 97 && guess <= 122) {
                                    guessV4[(int) guess - 15 - 97].setStroke(Color.WHITE);
                                }
                            }
                            else if (temp - 20 < 5) {
                                if (guess >= 65 && guess <= 90) {
                                    guessV5[(int) guess - 20 - 65].setStroke(Color.WHITE);
                                }
                                if (guess >= 97 && guess <= 122) {
                                    guessV5[(int) guess - 20 - 97].setStroke(Color.WHITE);
                                }
                            }
                            else if (temp - 25 < 5) {
                                if (guess >= 65 && guess <= 90) {
                                    guessV6[(int) guess - 25 - 65].setStroke(Color.WHITE);
                                }
                                if (guess >= 97 && guess <= 122) {
                                    guessV6[(int) guess - 25 - 97].setStroke(Color.WHITE);
                                }
                            }
                        }
                    }
                });
                if (gamedata.getRemainingGuesses() <= 0 || success)
                    stop();

            }

            @Override
            public void stop() {
                super.stop();
                end();
            }
        };
        timer.start();
    }

    private boolean alreadyGuessed(char c) {
        return gamedata.getGoodGuesses().contains(c) || gamedata.getBadGuesses().contains(c);
    }

    @Override
    public void handleNewRequest() {
        AppMessageDialogSingleton messageDialog   = AppMessageDialogSingleton.getSingleton();
        PropertyManager           propertyManager = PropertyManager.getManager();
        boolean                   makenew         = true;
        if (this.savable)
            try {
                makenew = promptToSave();
            } catch (IOException e) {
                messageDialog.show(propertyManager.getPropertyValue(NEW_ERROR_TITLE), propertyManager.getPropertyValue(NEW_ERROR_MESSAGE));
            }
        if (makenew) {
            appTemplate.getDataComponent().reset();                // reset the data (should be reflected in GUI)
            appTemplate.getWorkspaceComponent().reloadWorkspace(); // load data into workspace
            ensureActivatedWorkspace();                            // ensure workspace is activated
            workFile = null;                                       // new workspace has never been saved to a file
            currentWorkFile = null;

            Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
            gameWorkspace.reinitialize();
            enableGameButton();
            savable = false;
            AppGUI.updateToolbarControls(!savable);
        }

        if (gameover) {
            savable = false;
            appTemplate.getGUI().updateWorkspaceToolbar(savable);
            Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
            gameWorkspace.reinitialize();
            enableGameButton();
        }

    }

    @Override
    public void handleSaveRequest() throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();
        try {
            if (currentWorkFile != null)
                saveWork(currentWorkFile);
            else {
                FileChooser fileChooser = new FileChooser();
                File initialDir = new File("./saved/");
                fileChooser.setInitialDirectory(initialDir);
                fileChooser.setTitle(propertyManager.getPropertyValue(SAVE_WORK_TITLE));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(propertyManager.getPropertyValue(WORK_FILE_EXT_DESC),
                        "*.json"));
                File selectedFile = fileChooser.showSaveDialog(appTemplate.getGUI().getWindow());
                if (selectedFile != null)
                    saveWork(selectedFile);
            }
        } catch (IOException ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(propertyManager.getPropertyValue(SAVE_ERROR_TITLE), propertyManager.getPropertyValue(SAVE_ERROR_MESSAGE));
        }
    }

    private void saveWork(File selectedFile) throws IOException {
        appTemplate.getFileComponent()
                .saveData(appTemplate.getDataComponent(), Paths.get(selectedFile.getAbsolutePath()));

        currentWorkFile = selectedFile;
        savable = false;

        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        PropertyManager           props  = PropertyManager.getManager();
        dialog.show(props.getPropertyValue(SAVE_COMPLETED_TITLE), props.getPropertyValue(SAVE_COMPLETED_MESSAGE));
    }

    @Override
    public void handleLoadRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (savable) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave();
            }

            // IF THE USER REALLY WANTS TO OPEN A Course
            if (continueToOpen) {
                // GO AHEAD AND PROCEED LOADING A Course
                promptToOpen();
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            PropertyManager props = PropertyManager.getManager();
            dialog.show(props.getPropertyValue(LOAD_ERROR_TITLE), props.getPropertyValue(LOAD_ERROR_MESSAGE));
        }
    }

    private void promptToOpen() {
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
        PropertyManager props = PropertyManager.getManager();

        // AND NOW ASK THE USER FOR THE FILE TO OPEN
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("./saved/"));
        fc.setTitle(props.getPropertyValue(LOAD_WORK_TITLE));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(props.getPropertyValue(WORK_FILE_EXT_DESC), "*.json"));
        File selectedFile = fc.showOpenDialog(appTemplate.getGUI().getWindow());
        currentWorkFile = selectedFile;
        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                AppDataComponent dataManager = appTemplate.getDataComponent();
                AppFileComponent fileManager = appTemplate.getFileComponent();
                setGamedata(appTemplate);

                fileManager.loadData(gamedata, selectedFile.getAbsolutePath());
                appTemplate.setGameData(gamedata);

                appTemplate.getWorkspaceComponent().reloadWorkspace(); // load data into workspace
                ensureActivatedWorkspace();                            // ensure workspace is activated

                savable = false;
                appTemplate.getGUI().updateToolbarControls(savable);
                appTemplate.setGameData(gamedata);

                c = new Canvas(300, 600);
                gameover = false;
                success = false;
                savable = true;
                discovered = 0;
                Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
                appTemplate.getGUI().updateWorkspaceToolbar(savable);
                HBox remainingGuessBox = gameWorkspace.getRemainingGuessBox();
                HBox guessedLetters    = (HBox) gameWorkspace.getGameTextsPane().getChildren().get(1);
                HBox stickMan = gameWorkspace.getStickMan();
                initCanvas(stickMan);
                updateCanvas(gamedata);
                remains = new Label(Integer.toString(gamedata.getRemainingGuesses()));
                remainingGuessBox.getChildren().addAll(new Label("Remaining Guesses: "), remains);

                StackPane sP = new StackPane();
                HBox tHB = new HBox();
                char[] targetword = gamedata.getTargetWord().toCharArray();
                progress = new Text[targetword.length * 3];
                for (int i = 0; i < progress.length; i++) {
                    progress[i] = new Text("  ");
                    progress[i].setFont(Font.font("Monospaced", 20));
                    i++;
                    progress[i] = new Text(Character.toString(targetword[(i - 1)/3]));
                    progress[i].setFont(Font.font("Monospaced", 20));
                    if (gamedata.getGoodGuesses().contains(targetword[(i - 1)/3])) {
                        progress[i].setVisible(true);
                        discovered++;
                    }
                    else {
                        progress[i].setVisible(false);
                    }
                    i++;
                    progress[i] = new Text("  ");
                    progress[i].setFont(Font.font("Monospaced", 20));
                }
                Canvas tCa = new Canvas(21 * progress.length, 40);
                GraphicsContext gc = tCa.getGraphicsContext2D();
                gc.setFill(Color.TEAL);
                gc.fillRect(0, 0, 21 * progress.length, 40);
                gc.setFill(Color.PINK);
                for (int i = 0; i < progress.length/3; i++) {
                    gc.fillRect((63*i) + 2, 2, 59, 36);
                }
                tHB.getChildren().addAll(progress);
                sP.getChildren().addAll(tCa);
                sP.getChildren().addAll(tHB);
                sP.setAlignment(Pos.TOP_RIGHT);
                StackPane.setMargin(tHB, new Insets(10, 0, 10, 70));
                guessedLetters.getChildren().addAll(sP);

                c1 = new Canvas(300, 360);
                gc = c1.getGraphicsContext2D();
                gc.setFill(Color.GOLD);
                gc.fillRect(0, 0, 300, 300);
                gc.fillRect(0, 0, 60, 360);
                StackPane sp = new StackPane();
                VBox vB = new VBox(40);
                HBox hB;
                guessV1 = new Text[5];
                int j = 0;
                for (int i = 65; i < 70; i++) {
                    guessV1[j] = new Text("  " + Character.toString((char) i) + "  ");
                    guessV1[j].setFont(Font.font("Monospaced", 20));
                    //guessV[j].setVisible(false);
                    j++;
                }
                hB = new HBox();
                hB.getChildren().addAll(guessV1);
                vB.getChildren().addAll(hB);

                guessV2 = new Text[5];
                j = 0;
                for (int i = 70; i < 75; i++) {
                    guessV2[j] = new Text("  " + Character.toString((char) i) + "  ");
                    guessV2[j].setFont(Font.font("Monospaced", 20));
                    //guessV[j].setVisible(false);
                    j++;
                }
                hB = new HBox();
                hB.getChildren().addAll(guessV2);
                vB.getChildren().addAll(hB);
                guessV3 = new Text[5];
                j = 0;
                for (int i = 75; i < 80; i++) {
                    guessV3[j] = new Text("  " + Character.toString((char) i) + "  ");
                    guessV3[j].setFont(Font.font("Monospaced", 20));
                    //guessV[j].setVisible(false);
                    j++;
                }
                hB = new HBox();
                hB.getChildren().addAll(guessV3);
                vB.getChildren().addAll(hB);
                guessV4 = new Text[5];
                j = 0;
                for (int i = 80; i < 85; i++) {
                    guessV4[j] = new Text("  " + Character.toString((char) i) + "  ");
                    guessV4[j].setFont(Font.font("Monospaced", 20));
                    //guessV[j].setVisible(false);
                    j++;
                }
                hB = new HBox();
                hB.getChildren().addAll(guessV4);
                vB.getChildren().addAll(hB);
                guessV5 = new Text[5];
                j = 0;
                for (int i = 85; i < 90; i++) {
                    guessV5[j] = new Text("  " + Character.toString((char) i) + "  ");
                    guessV5[j].setFont(Font.font("Monospaced", 20));
                    //guessV[j].setVisible(false);
                    j++;
                }
                hB = new HBox();
                hB.getChildren().addAll(guessV5);
                vB.getChildren().addAll(hB);

                guessV6 = new Text[1];
                guessV6[0] = new Text("  " + "Z" + "  ");
                guessV6[0].setFont(Font.font("Monospaced", 20));
                //guessV[0].setVisible(false);
                hB = new HBox();
                hB.getChildren().addAll(guessV6);
                vB.getChildren().addAll(hB);

                sp.getChildren().addAll(c1);
                sp.getChildren().addAll(vB);
                sp.setAlignment(Pos.TOP_LEFT);

                guessedLetters.getChildren().addAll(sp);

                for (int k = 0; k < 5; k++) {
                    if (gamedata.getBadGuesses().contains((char) (97 + k))) {
                        guessV1[k].setStroke(Color.WHITE);
                    }
                    else if (gamedata.getGoodGuesses().contains((char) (97 + k))){
                        guessV1[k].setStroke(Color.WHITE);
                    }
                }
                for (int k = 5; k < 10; k++) {
                    if (gamedata.getBadGuesses().contains((char) (97 + k))) {
                        guessV2[k - 5].setStroke(Color.WHITE);
                    }
                    else if (gamedata.getGoodGuesses().contains((char) (97 + k))){
                        guessV2[k - 5].setStroke(Color.WHITE);
                    }
                }
                for (int k = 10; k < 15; k++) {
                    if (gamedata.getBadGuesses().contains((char) (97 + k))) {
                        guessV3[k - 10].setStroke(Color.WHITE);
                    }
                    else if (gamedata.getGoodGuesses().contains((char) (97 + k))){
                        guessV3[k - 10].setStroke(Color.WHITE);
                    }
                }
                for (int k = 15; k < 20; k++) {
                    if (gamedata.getBadGuesses().contains((char) (97 + k))) {
                        guessV4[k - 15].setStroke(Color.WHITE);
                    }
                    else if (gamedata.getGoodGuesses().contains((char) (97 + k))){
                        guessV4[k - 15].setStroke(Color.WHITE);
                    }
                }
                for (int k = 20; k < 25; k++) {
                    if (gamedata.getBadGuesses().contains((char) (97 + k))) {
                        guessV5[k - 20].setStroke(Color.WHITE);
                    }
                    else if (gamedata.getGoodGuesses().contains((char) (97 + k))){
                        guessV5[k - 20].setStroke(Color.WHITE);
                    }
                }
                for (int k = 25; k < 26; k++) {
                    if (gamedata.getBadGuesses().contains((char) (97 + k))) {
                        guessV6[k - 25].setStroke(Color.WHITE);
                    }
                    else if (gamedata.getGoodGuesses().contains((char) (97 + k))){
                        guessV6[k - 25].setStroke(Color.WHITE);
                    }
                }

                if (is7More(gamedata.getTargetWord())) {
                    hint = new Button();
                    hint.setText("HINT");
                    hint.setOnAction(e -> {
                        String tS = "";
                        char tC = ' ';
                        Random r = new Random();
                        int n = r.nextInt(gamedata.getTargetWord().length() - discovered);
                        for (int i = 0; i < gamedata.getTargetWord().length(); i++) {
                            if (!gamedata.getGoodGuesses().contains(gamedata.getTargetWord().charAt(i))) {
                                tS = tS + gamedata.getTargetWord().charAt(i);
                            }
                        }
                        tC = tS.charAt(n);

                        for (int i = 0; i < ((progress.length) / 3); i++) {
                            boolean k = true;
                            if (gamedata.getTargetWord().charAt(i) == Character.toLowerCase(tC)) {
                                progress[(3 * i) + 1].setVisible(true);
                                discovered++;
                                if (k) {
                                    gamedata.addGoodGuess(tC);
                                }
                                k = false;
                            }
                        }
                        gamedata.setRemainingGuesses(gamedata.getRemainingGuesses() - 1);
                        updateCanvas(gamedata);
                        success = (discovered == gamedata.getTargetWord().length());
                        remains.setText(Integer.toString(gamedata.getRemainingGuesses()));
                        savable = true;
                        AppGUI.updateToolbarControls(!savable);
                        /*
                        if (tC >= 65 && tC <= 90) {
                            guessV[(int) tC - 65].setVisible(true);
                        }
                        if (tC >= 97 && tC <= 122) {
                            guessV[(int) tC - 97].setVisible(true);
                        }
                        */
                        hint.setDisable(true);
                    });
                    stickMan.getChildren().add(hint);
                    if (gamedata.getBadGuesses().size() + gamedata.getRemainingGuesses() != 10) {
                        hint.setDisable(true);
                    }
                }
                enableGameButton();
                gameButton.setDisable(true);

                play();
            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getPropertyValue(LOAD_ERROR_TITLE), props.getPropertyValue(LOAD_ERROR_MESSAGE));
            }
        }
    }

    @Override
    public void handleExitRequest() {
        try {
            boolean exit = true;
            if (savable)
                exit = promptToSave();
            if (exit)
                System.exit(0);
        } catch (IOException ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            PropertyManager           props  = PropertyManager.getManager();
            dialog.show(props.getPropertyValue(SAVE_ERROR_TITLE), props.getPropertyValue(SAVE_ERROR_MESSAGE));
        }
    }

    private void ensureActivatedWorkspace() {
        appTemplate.getWorkspaceComponent().activateWorkspace(appTemplate.getGUI().getAppPane());
    }

    private boolean promptToSave() throws IOException {
        {
            PropertyManager            propertyManager   = PropertyManager.getManager();
            YesNoCancelDialogSingleton yesNoCancelDialog = YesNoCancelDialogSingleton.getSingleton();

            yesNoCancelDialog.show(propertyManager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE),
                    propertyManager.getPropertyValue(SAVE_UNSAVED_WORK_MESSAGE));

            if (yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
                if (currentWorkFile != null)
                    saveWork(currentWorkFile);
                else {
                    FileChooser filechooser = new FileChooser();
                    URL         workDirURL  = AppTemplate.class.getClassLoader().getResource(APP_WORKDIR_PATH.getParameter());
                    if (workDirURL == null)
                        throw new FileNotFoundException("Work folder not found under resources.");

                    File initialDir = new File(workDirURL.getFile());
                    filechooser.setInitialDirectory(initialDir);
                    filechooser.setTitle(propertyManager.getPropertyValue(SAVE_WORK_TITLE));

                    String description = propertyManager.getPropertyValue(WORK_FILE_EXT_DESC);
                    String extension   = "*.json";
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(String.format("%s (*.%s)", description, extension),
                            String.format("*.%s", extension));
                    filechooser.getExtensionFilters().add(extFilter);
                    File selectedFile = filechooser.showSaveDialog(appTemplate.getGUI().getWindow());
                    if (selectedFile != null)
                        saveWork(selectedFile);
                }
            }

            return !yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.CANCEL);
        }
    }

    /**
     * A helper method to save work. It saves the work, marks the current work file as saved, notifies the user, and
     * updates the appropriate controls in the user interface
     *
     * @param target The file to which the work will be saved.
     * @throws IOException
     */
    private void save(Path target) throws IOException {

    }
}