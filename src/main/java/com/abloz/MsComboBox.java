package com.abloz;

import java.awt.*;
import java.util.Set;

/**
 * MultiSelected Combo Box
 */
public class MsComboBox  extends javax.swing.JComboBox implements java.awt.event.FocusListener {
    protected java.util.Set indexSet = new java.util.HashSet();
    javax.swing.JTextField jTextField = null;

    public Set getIndexSet() {
        return indexSet;
    }

    public MsComboBox(String str[][]) {
        setEditable(true);
        setModel(new MsDefaultComboBoxModel(str));
        setUI(new MsMetalComboBoxUI(this));
        setRenderer(new MsJCheckBox());
        setSelectedIndex(-1);
        jTextField = (javax.swing.JTextField) getEditor().getEditorComponent();

        setPreferredSize(new Dimension(80,8));

    }
    public MsComboBox(){
        setEditable(true);
        setModel(new MsDefaultComboBoxModel());
        setUI(new MsMetalComboBoxUI(this));
        setRenderer(new MsJCheckBox());
        setSelectedIndex(-1);
        jTextField = (javax.swing.JTextField) getEditor().getEditorComponent();
        setPreferredSize(new Dimension(80,10));
    }
    public void addItem(String item){
        MsDefaultComboBoxModel model = (MsDefaultComboBoxModel)getModel();
        model.addItem(item);
    }

    /**
     * set text to editor
     * @param text
     */
    public void setText(String text){
        jTextField.setText(text);
    }

    public String getText(){
        return jTextField.getText();
    }
    public void focusGained(java.awt.event.FocusEvent e) {
    }

    public void focusLost(java.awt.event.FocusEvent e) {
//            这是一种特殊情况下的使用方法
//            java.awt.Container container=(java.awt.Container)getEditor().getEditorComponent();
//            while(container!=null&&!(container instanceof  javax.swing.JLabel))
//            {
//                container=container.getParent();
//            }
//            ((cLabel)container).setValue(((javax.swing.JTextField)getEditor().getEditorComponent()).getText());
    }
    public class MsMetalComboBoxUI extends javax.swing.plaf.metal.MetalComboBoxUI {
        private MsMetalComboBoxUI(MsComboBox msComboBox) {
            this.comboBox = msComboBox;
        }

        protected javax.swing.plaf.basic.ComboPopup createPopup() {
            return new MsBasicComboPopup(comboBox);
        }
    }

    public class MsBasicComboPopup extends javax.swing.plaf.basic.BasicComboPopup {
        public MsBasicComboPopup(javax.swing.JComboBox combo) {
            super(combo);
        }

        protected void configureList() {
            super.configureList();
            list.setSelectionModel(new MsDefaultListSelectionModel(comboBox));
        }

        protected java.awt.event.MouseListener createListMouseListener() {
            return new MsMouseAdapter(list, comboBox);
        }
    }

    private class MsDefaultListSelectionModel extends javax.swing.DefaultListSelectionModel {
        protected MsComboBox msComboBox;

        public MsDefaultListSelectionModel(javax.swing.JComboBox comboBox) {
            this.msComboBox = (MsComboBox) comboBox;
        }

        public boolean isSelectedIndex(int index) {
            return msComboBox.indexSet.contains(index);
        }
    }

    private class MsMouseAdapter extends java.awt.event.MouseAdapter {
        protected javax.swing.JList list;
        protected MsComboBox msComboBox;

        private MsMouseAdapter(javax.swing.JList list, javax.swing.JComboBox comboBox) {
            this.list = list;
            this.msComboBox = (MsComboBox) comboBox;
        }

        public void mousePressed(java.awt.event.MouseEvent anEvent) {
            StringBuilder sb, sb1, sb2, sb3;
            int k, index;
            index = list.getSelectedIndex();
            javax.swing.JTextField jTextField = (javax.swing.JTextField) msComboBox.getEditor().getEditorComponent();

            //获取已有的输入结果
            String txt = jTextField.getText();
            if(txt.equals("*") || txt.equals("?") || txt.trim().length()<=0){
                txt="";
            }

            sb = new StringBuilder(txt);

            if (sb.length() > 0 && ',' != sb.charAt(0))
                sb.insert(0, ",");
            if (sb.length() > 0 && ',' != sb.charAt(sb.length() - 1))
                sb.append(",");
            if (sb.length() > 0 && ",".equals(sb.toString()))
                sb = new StringBuilder();

            //多选
            if (list.getSelectionMode() == javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
                // 已经选择过，移除
                if (msComboBox.indexSet.contains(index)) {
                    msComboBox.indexSet.remove(index);
                    sb1 = new StringBuilder();
                    sb1.append(",").append(msComboBox.getModel().getElementAt(index)).append(",");
                    k = sb.indexOf(sb1.toString());
                    while (k != -1) {
                        sb.replace(k, k + sb1.length(), ",");
                        k = sb.indexOf(sb1.toString());
                    }
                } else {//没选择过
                    msComboBox.indexSet.add(index);
                    if (sb.length() == 0)
                        sb.append(",").append(msComboBox.getModel().getElementAt(index)).append(",");
                    else
                        sb.append(msComboBox.getModel().getElementAt(index)).append(",");
                }
            } else {//单选
                if (!msComboBox.indexSet.contains(index)) {
                    msComboBox.indexSet.clear();
                    msComboBox.indexSet.add(index);
                }
            }

            Object obj;
            sb2 = new StringBuilder(sb);
            // 替换完正常的可选值
            for (int i = 0; i < list.getModel().getSize(); i++) {
                obj = list.getModel().getElementAt(i);
                sb1 = new StringBuilder();
                sb1.append(",").append(obj).append(",");
                k = sb2.indexOf(sb1.toString());
                while (k != -1) {
                    sb2.replace(k, k + sb1.length(), ",");
                    k = sb2.indexOf(sb1.toString());
                }
            }
            java.util.List list1 = new java.util.ArrayList(msComboBox.indexSet);
            java.util.Collections.sort(list1);
            for (int i = 0; i < list1.size(); i++) {
                obj = msComboBox.getModel().getElementAt(Integer.parseInt(list1.get(i).toString()));
                sb1 = new StringBuilder();
                sb1.append(",").append(obj).append(",");
                k = sb.indexOf(sb1.toString());
                if (k != -1 && sb2.indexOf(sb1.toString()) == -1) {
                    sb2.append(obj).append(",");
                }
            }
            sb = new StringBuilder(sb2);
            if (sb.length() > 0 && ',' == sb.charAt(0))
                sb.deleteCharAt(0);
            if (sb.length() > 0 && ',' == sb.charAt(sb.length() - 1))
                sb.deleteCharAt(sb.length() - 1);
            if (sb.length() > 0 && ",".equals(sb.toString()))
                sb = new StringBuilder();
            jTextField.setText(sb.toString());

            msComboBox.repaint();
            list.repaint();
        }
    }

    public class MsDefaultComboBoxModel extends javax.swing.DefaultComboBoxModel {
        public MsDefaultComboBoxModel(String[][] str) {
            for (int i = 0; i < str.length; i++)
                addElement(str[i][0]);
        }
        public MsDefaultComboBoxModel(){}
        public void addItem(String item) {
            addElement(item);
        }
        public void addItems(String[][] items) {
            for (int i = 0; i < items.length; i++)
                addElement(items[i][0]);

        }

    }

    public class MsJCheckBox extends javax.swing.JCheckBox implements javax.swing.ListCellRenderer {
        public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index,
                                                               boolean isSelected, boolean cellHasFocus) {
            setComponentOrientation(list.getComponentOrientation());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setSelected(isSelected);
            setText(value == null ? "" : value.toString());
            setFont(list.getFont());
            return this;
        }
    }
//    public static void main(String[] args) {
//        String str[][] = new String[10][3];
//        for (int i = 0; i < str.length; i++) {
//            str[i][0] = i + 6 + "";
//        }
//        javax.swing.JComboBox jComboBox = new MsComboBox(str);
//        jComboBox.setEditable(true);
////        jComboBox.setPreferredSize(new java.awt.Dimension(400, 60));
//
//        javax.swing.JTextField jTextField = new javax.swing.JTextField(40);
//        // 测试焦点事件
//
//        javax.swing.JFrame frame = new javax.swing.JFrame();
//        frame.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//        frame.setSize(500, 500);
//        frame.setLocationRelativeTo(null);
//        frame.add(jComboBox);
//        frame.add(jTextField);
//        frame.setVisible(true);
//    }
}
