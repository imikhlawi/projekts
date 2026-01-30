import javax.swing.*;
import java.awt.*;


public class CheckboxRenderer extends JCheckBox implements ListCellRenderer<String> {

    public CheckboxRenderer() {
        super();
        setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setText(value);
        setSelected(isSelected);
        return this;
    }
}
