package settings;

/**
 * @author Ritwik Banerjee
 */
public enum InitializationParameters {

    APP_PROPERTIES_XML("app-properties.xml"),
    APP_PROPERTIES_XML_KR("app-properties_KR.xml"),
    WORKSPACE_PROPERTIES_XML("workspace-properties.xml"),
    WORKSPACE_PROPERTIES_XML_KR("workspace-properties_KR.xml"),
    PROPERTIES_SCHEMA_XSD("properties-schema.xsd"),
    ERROR_DIALOG_BUTTON_LABEL("Exit application."),
    APP_WORKDIR_PATH("./saved/"),
    APP_IMAGEDIR_PATH("images");

    private String parameter;

    InitializationParameters(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}