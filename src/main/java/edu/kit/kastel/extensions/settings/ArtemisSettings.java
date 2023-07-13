package edu.kit.kastel.extensions.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.JBColor;
import edu.kit.kastel.extensions.guis.SettingsContent;
import edu.kit.kastel.sdq.artemis4j.api.ArtemisClientException;
import edu.kit.kastel.sdq.artemis4j.client.RestClientManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

public class ArtemisSettings implements Configurable {

  private static final String SETTINGS_DIALOG_NAME = "IntelliGrade Settings";
  private static final String LOGIN_ERROR_DIALOG_TITLE = "Error logging in!";

  //set up automated GUI and generate necessary bindings
  private final JPanel contentPanel = new JPanel();
  private final SettingsContent generatedMenu = new SettingsContent();
  private final JPasswordField pwdInput = generatedMenu.getInputPwd();
  private final JTextField usernameField = generatedMenu.getInputUsername();
  private final JLabel loggedInLabel = generatedMenu.getLoggedInLabel();
  private final JTextField artemisUrlField = generatedMenu.getArtemisUrlInput();

  /**
   * Returns the visible name of the configurable component.
   * Note, that this method must return the display name
   * that is equal to the display name declared in XML
   * to avoid unexpected errors.
   *
   * @return the visible name of the configurable component
   */
  @Override
  public @NlsContexts.ConfigurableName String getDisplayName() {
    return SETTINGS_DIALOG_NAME;
  }

  /**
   * Creates a new Swing form that enables the user to configure the settings.
   * Usually this method is called on the EDT, so it should not take a long time.
   * <p>
   * Also, this place is designed to allocate resources (subscriptions/listeners etc.)
   *
   * @return new Swing form to show, or {@code null} if it cannot be created
   * @see #disposeUIResources
   */
  @Override
  public @Nullable JComponent createComponent() {
    contentPanel.setLayout(new GridLayout());
    contentPanel.add(generatedMenu);
    //add action listener to login Button
    generatedMenu.getBtnLogin().addActionListener(this::loginButtonListener);

    return contentPanel;
  }

  /**
   * Listener Method that gets called when the login Button is pressed
   * This method will Log in the User
   *
   * @param actionEvent The Event passed by AWT is the Button is pressed
   */
  private void loginButtonListener(ActionEvent actionEvent) {
    //set label if login was successful
    setLabelOnLoginSuccess();
  }

  /**
   * Indicates whether the Swing form was modified or not.
   * This method is called very often, so it should not take a long time.
   *
   * @return {@code true} if the settings were modified, {@code false} otherwise
   */
  @Override
  public boolean isModified() {
    ArtemisSettingsState settings = ArtemisSettingsState.getInstance();
    //check if all three parameters are equal
    String password = new String(pwdInput.getPassword());
    boolean modified = !password.equals(settings.artemisPassword);
    modified |= !usernameField.getText().equals(settings.username);
    modified |= !artemisUrlField.getText().equals(settings.artemisInstanceUrl);
    return modified;
  }

  /**
   * Stores the settings from the Swing form to the configurable component.
   * This method is called on EDT upon user's request.
   *
   * @throws ConfigurationException if values cannot be applied
   */
  @Override
  public void apply() throws ConfigurationException {
    //store all settings persistently
    ArtemisSettingsState settings = ArtemisSettingsState.getInstance();
    settings.artemisInstanceUrl = artemisUrlField.getText();
    settings.username = usernameField.getText();
    settings.artemisPassword = new String(pwdInput.getPassword());
  }

  /**
   * Loads the settings from the configurable component to the Swing form.
   * This method is called on EDT immediately after the form creation or later upon user's request.
   */
  @Override
  public void reset() {
    ArtemisSettingsState settings = ArtemisSettingsState.getInstance();
    artemisUrlField.setText(settings.artemisInstanceUrl);
    usernameField.setText(settings.username);
    pwdInput.setText(settings.artemisPassword);

    setLabelOnLoginSuccess();
  }


  /**
   * This method will create a new Connection to Artemis and try to log in.
   * If login was successful the label in the settings dialog will be set
   */
  private void setLabelOnLoginSuccess() {
    //create new Artemis Instance
    var artemisInstance = new RestClientManager(
            artemisUrlField.getText(),
            usernameField.getText(),
            new String(pwdInput.getPassword())
    );

    //try logging in and display error iff error occurred
    try {
      artemisInstance.login();
    } catch (ArtemisClientException e) {
      JOptionPane.showMessageDialog(contentPanel, e.getMessage(), LOGIN_ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    if (artemisInstance.isReady()) {
      loggedInLabel.setText("true");
      loggedInLabel.setForeground(JBColor.GREEN);
    }
  }
}
