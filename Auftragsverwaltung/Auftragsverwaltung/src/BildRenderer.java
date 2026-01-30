import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;


class BildRenderer extends DefaultTableCellRenderer
{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        JLabel label = new JLabel();

        if (value!=null) {
            label.setHorizontalAlignment(JLabel.CENTER);
            if(!(value instanceof String))
             label.setIcon(new ImageIcon((byte[])value));
        }

        return label;
    }
}