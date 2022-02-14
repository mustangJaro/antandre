package namespace.sort;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class AppSettingsComponent {
    private final JPanel myMainPanel;
    private final JBCheckBox myNewlineRequired = new JBCheckBox("Include newline after require?");

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(myNewlineRequired, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return myNewlineRequired;
    }

    public boolean getNewlineRequired() {
        return myNewlineRequired.isSelected();
    }

    public void setNewlineRequired(boolean newStatus) {
        myNewlineRequired.setSelected(newStatus);
    }
}
