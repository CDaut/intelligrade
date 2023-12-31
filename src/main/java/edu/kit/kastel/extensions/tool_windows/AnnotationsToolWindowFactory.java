package edu.kit.kastel.extensions.tool_windows;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import edu.kit.kastel.extensions.guis.AnnotationsViewContent;
import edu.kit.kastel.utils.ArtemisUtils;
import edu.kit.kastel.utils.AssessmentUtils;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;


/**
 * This class generates the tool Window for annotations in the bottom.
 */
public class AnnotationsToolWindowFactory implements ToolWindowFactory {

  //set up automated GUI and generate necessary bindings
  private final JPanel contentPanel = new JPanel();

  private final AnnotationsViewContent generatedMenu = new AnnotationsViewContent();

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    contentPanel.setLayout(new GridLayout());

    //give up if logging in to Artemis failed
    if (!ArtemisUtils.getArtemisClientInstance().isReady()) {
      return;
    }

    //add content to menu panel
    contentPanel.add(generatedMenu);
    Content content = ContentFactory.getInstance().createContent(
            this.contentPanel,
            null,
            false
    );

    toolWindow.getContentManager().addContent(content);

    //register the GUI to be updated when an annotation is added
    AssessmentUtils.registerAssessmentListener(generatedMenu);
  }
}
