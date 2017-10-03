package apptemplate;

import components.AppComponentsBuilder;
import components.AppDataComponent;
import components.AppFileComponent;
import components.AppWorkspaceComponent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import propertymanager.PropertyManager;
import settings.InitializationParameters;
import ui.AppGUI;
import ui.AppMessageDialogSingleton;
import ui.YesNoCancelDialogSingleton;
import xmlutils.InvalidXMLFileFormatException;

import java.io.File;
import java.net.URL;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.*;

/**
 * @author Richard McKenna, Ritwik Banerjee
 */
public abstract class AppTemplate extends Application {

    private final PropertyManager propertyManager = PropertyManager.getManager();
    private AppDataComponent      dataComponent; // to manage the app's data
    private AppFileComponent      fileComponent; // to manage the app's file I/O
    private AppWorkspaceComponent workspaceComponent; // to manage the app's GUI workspace
    private AppGUI                gui;
    boolean isKR = false;
    Button btn = new Button();

    public abstract AppComponentsBuilder makeAppBuilderHook();

    public void setGameData(AppDataComponent data) {
        this.dataComponent = data;
    }

    public AppDataComponent getDataComponent() {
        return dataComponent;
    }

    public AppFileComponent getFileComponent() {
        return fileComponent;
    }

    public AppWorkspaceComponent getWorkspaceComponent() {
        return workspaceComponent;
    }

    public AppGUI getGUI() {
        return gui;
    }

    @SuppressWarnings("unused")
    public String getFileControllerClass() {
        return "AppFileController";
    }

    @Override
    public void start(Stage primaryStage) {
        AppMessageDialogSingleton  messageDialog = AppMessageDialogSingleton.getSingleton();
        YesNoCancelDialogSingleton yesNoDialog   = YesNoCancelDialogSingleton.getSingleton();
        messageDialog.init(primaryStage);
        yesNoDialog.init(primaryStage);

        try {
            btn.setText("Korean");
            btn.setOnMouseClicked(e-> {
                try {
                    if (loadProperties(APP_PROPERTIES_XML_KR) && loadProperties(WORKSPACE_PROPERTIES_XML_KR)) {
                        AppComponentsBuilder builder = makeAppBuilderHook();

                        fileComponent = builder.buildFileComponent();
                        dataComponent = builder.buildDataComponent();
                        gui = (propertyManager.hasProperty(APP_WINDOW_WIDTH) && propertyManager.hasProperty(APP_WINDOW_HEIGHT))
                                ? new AppGUI(primaryStage, propertyManager.getPropertyValue(APP_TITLE.toString()), this,
                                Integer.parseInt(propertyManager.getPropertyValue(APP_WINDOW_WIDTH)),
                                Integer.parseInt(propertyManager.getPropertyValue(APP_WINDOW_HEIGHT)))
                                : new AppGUI(primaryStage, propertyManager.getPropertyValue(APP_TITLE.toString()), this);
                        workspaceComponent = builder.buildWorkspaceComponent();
                        initStylesheet();
                        gui.initStyle();
                        workspaceComponent.initStyle();
                    }
                }
                catch(Exception e1) {

                }
            });
            Button btn2 = new Button();
            btn2.setText("English");
            btn2.setOnMouseClicked(e-> {
                try {
                    if (loadProperties(APP_PROPERTIES_XML) && loadProperties(WORKSPACE_PROPERTIES_XML)) {
                        AppComponentsBuilder builder = makeAppBuilderHook();

                        fileComponent = builder.buildFileComponent();
                        dataComponent = builder.buildDataComponent();
                        gui = (propertyManager.hasProperty(APP_WINDOW_WIDTH) && propertyManager.hasProperty(APP_WINDOW_HEIGHT))
                                ? new AppGUI(primaryStage, propertyManager.getPropertyValue(APP_TITLE.toString()), this,
                                Integer.parseInt(propertyManager.getPropertyValue(APP_WINDOW_WIDTH)),
                                Integer.parseInt(propertyManager.getPropertyValue(APP_WINDOW_HEIGHT)))
                                : new AppGUI(primaryStage, propertyManager.getPropertyValue(APP_TITLE.toString()), this);
                        workspaceComponent = builder.buildWorkspaceComponent();
                        initStylesheet();
                        gui.initStyle();
                        workspaceComponent.initStyle();
                    }
                }
                catch (Exception e2) {

                }
            });
            HBox root = new HBox();
            root.getChildren().add(btn);
            root.getChildren().add(btn2);

            Scene scene = new Scene(root, 300, 250);

            primaryStage.setTitle("Choose your language");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(propertyManager.getPropertyValue(PROPERTIES_LOAD_ERROR_TITLE.toString()),
                        propertyManager.getPropertyValue(PROPERTIES_LOAD_ERROR_MESSAGE.toString()));
        }
    }

    public boolean loadProperties(InitializationParameters propertyParameter) {
        try {
            propertyManager.loadProperties(AppTemplate.class, propertyParameter.getParameter(), PROPERTIES_SCHEMA_XSD.getParameter());
        } catch (InvalidXMLFileFormatException e) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(propertyManager.getPropertyValue(PROPERTIES_LOAD_ERROR_TITLE.toString()),
                        propertyManager.getPropertyValue(PROPERTIES_LOAD_ERROR_MESSAGE.toString()));
            return false;
        }

        return true;
    }

    public void initStylesheet() {
        URL cssResource = getClass().getClassLoader().getResource(propertyManager.getPropertyValue(APP_PATH_CSS) +
                                                                  File.separator +
                                                                  propertyManager.getPropertyValue(APP_CSS));
        assert cssResource != null;
        gui.getPrimaryScene().getStylesheets().add(cssResource.toExternalForm());
    }
}
