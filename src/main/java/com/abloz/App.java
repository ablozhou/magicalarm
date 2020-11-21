package com.abloz;

/**
 * Hello world!
 *
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class App {


    //创建窗体，JFrame
    JFrame f = new JFrame("测试");

    //定义一个按钮，并为之指定图标
    //ImageIcon(Image image)
    //根据图像对象创建一个 ImageIcon。  Icon是接口，ImageIcon实现了该接口，多态的体现
    //定义一个单选按钮，并为之指定图标
    Icon okIcon = new ImageIcon("ico/ok.png");
    JButton ok = new JButton("确认", okIcon);
    //定义一个单选按钮，出事处于选中状态
    //JradioButton  实现一个单选按钮，此按钮项可被选择或取消选择，并可为用户显示其状态。
    //JRadioButton(String text, boolean selected)
    //创建一个具有指定文本和选择状态的单选按钮。
    JRadioButton male = new JRadioButton("男", true);
    JRadioButton female = new JRadioButton("女", false);
    ButtonGroup bg = new ButtonGroup();
    //定义一个复选框，出事状态处于没有选中状态
    JCheckBox married = new JCheckBox("是否已婚？", false);
    String[] colors = new String[]{"红色", "绿色", "蓝色"};
    //定义一个下拉选择框
    //JComboBox(Object[] items)
    //创建包含指定数组中的元素的 JComboBox。

    JComboBox<String> colorChooser = new JComboBox<>(colors);
    //定义一个列表选择框？？？？？？？？？？？？？？
    //JList(Object[] listData)
    //构造一个 JList，使其显示指定数组中的元素。
    JList<String> colorList = new JList<>(colors);
    //定义一个8行20列的多行文本框
    JTextArea ta = new JTextArea(8, 20);
    //定义一个40列的单行文本框
    JTextField name = new JTextField(40);

    JMenuBar mb = new JMenuBar();
    JMenu file = new JMenu("文件");
    //JMenu(String s)
    //构造一个新 JMenu，用提供的字符串作为其文本。
    JMenu edit = new JMenu("编辑");

    Icon newIcon = new ImageIcon("ico/new.png");
    //JMenuItem(String text, Icon icon)
    //创建带有指定文本和图标的 JMenuItem。
    JMenuItem newItem = new JMenuItem("新建", newIcon);

    Icon saveIcon = new ImageIcon("ico/new.png");
    JMenuItem saveItem = new JMenuItem("保存", saveIcon);

    Icon exitIcon = new ImageIcon("ico/exit.png");
    JMenuItem exitItem = new JMenuItem("退出", exitIcon);
    JCheckBoxMenuItem autoWrap = new JCheckBoxMenuItem("自动换行");

    JMenuItem copyItem = new JMenuItem("复制", new ImageIcon("ico/copy.png"));

    JMenuItem pasteItem = new JMenuItem("粘贴", new ImageIcon("ico/paste.png"));

    JMenu format = new JMenu("格式");

    JMenuItem commentItem = new JMenuItem("注释");

    JMenuItem cancelItem = new JMenuItem("取消注释");
    //定义一个右键菜单，用于设定程序的外观风格
    JPopupMenu pop = new JPopupMenu();
    ButtonGroup flavorGroup = new ButtonGroup();
    //创建5个单选按钮，用于设定程序的外观风格
    JRadioButtonMenuItem metalItem = new JRadioButtonMenuItem("Metal风格", true);
    JRadioButtonMenuItem nimbusItem = new JRadioButtonMenuItem("Nimbus风格");
    JRadioButtonMenuItem windowsItem = new JRadioButtonMenuItem("windows风格");
    JRadioButtonMenuItem motifyItem = new JRadioButtonMenuItem("motifyItem风格");
    JRadioButtonMenuItem classicItem = new JRadioButtonMenuItem("windows经典风格");

    /*------------------下面是用于执行界面初始化的init方法-------------------------*/
    public void init() {
        //创建一个装载了文本框、按钮的JPanel
        JPanel bottom = new JPanel();
        bottom.add(name);
        bottom.add(ok);
        f.add(bottom, BorderLayout.SOUTH);//用BorderLayout布局管理器，放在最底层
        //创建一个装载了下拉选择框、三个JCheckBox的JPanel
        JPanel checkPanel = new JPanel();
        checkPanel.add(colorChooser);
        //既要加载在一个ButtonGroup中，也要加载在checkPanel中
        //若去掉bg.add(male);bg.add(female); 则能选两个性别
        //若去掉 checkPanel.add(male);checkPanel.add(female); 则两个性别RadioButton消失
        bg.add(male);
        bg.add(female);
        checkPanel.add(male);
        checkPanel.add(female);
        checkPanel.add(married);
        //创建一个垂直排列的组件的Box，盛装多行文本域JPanel
        Box topLeft = Box.createVerticalBox();//创建一个从上到下显示的Box
        //使用JScrollPance作为普通组件的JViewPort
        JScrollPane taJsp = new JScrollPane(ta);
        topLeft.add(taJsp);
        topLeft.add(checkPanel);
        //创建一个水平排列组件的Box，盛装topLeft、colorList
        Box top = Box.createHorizontalBox();
        top.add(topLeft);
        top.add(colorList);
        //将Top Box容器添加到窗口的中间
        f.add(top);
        //----------下面开始组合菜单，并未菜单添加监听器---------------------
        //为newItem设置快捷键，设置快捷键时要使用大写字母
        newItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ta.append("用户单击了“新建”菜单\n");
            }
        });
        //为file菜单添加菜单项
        file.add(newItem);
        file.add(saveItem);
        file.add(exitItem);

        //为exit菜单添加菜单项
        edit.add(autoWrap);
        //使用addSeparator方法添加菜单分隔线
        edit.addSeparator();
        edit.add(copyItem);
        edit.add(pasteItem);
        //为commentItem组件添加提示信息
        commentItem.setToolTipText("将从程序代码注释起来！");
        //为format菜单添加菜单项
        format.add(commentItem);
        format.add(cancelItem);

        //使用添加new JMenuItem（“-”）的方式不能添加菜单分隔符
        edit.add(new JMenuItem("-"));
        //将format菜单组合到eidt菜单中，从而形成二级菜单
        edit.add(format);
        //将file、edit菜单添加到mb菜单条中
        mb.add(file);
        mb.add(edit);
        //为f窗口设置菜单条
        f.setJMenuBar(mb);

        //------下面开始组合右键菜单，并安装右键菜单-----------------
        flavorGroup.add(metalItem);
        flavorGroup.add(nimbusItem);
        flavorGroup.add(windowsItem);
        flavorGroup.add(classicItem);
        flavorGroup.add(motifyItem);

        pop.add(metalItem);
        pop.add(nimbusItem);
        pop.add(windowsItem);
        pop.add(classicItem);
        pop.add(motifyItem);

        //为5个风格菜单创建事件监听器
        ActionListener flavorListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    switch (e.getActionCommand()) {
                        case "Metal风格":
                            changeFlavor(1);
                            break;
                        case "Nimbus风格":
                            changeFlavor(2);
                            break;
                        case "Windows风格":
                            changeFlavor(3);
                            break;
                        case "Windows经典风格":
                            changeFlavor(4);
                            break;
                        case "Motif风格":
                            changeFlavor(5);
                            break;
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        };

        //为5个风格菜单项添加事件监听器

        metalItem.addActionListener(flavorListener);
        nimbusItem.addActionListener(flavorListener);
        windowsItem.addActionListener(flavorListener);
        classicItem.addActionListener(flavorListener);
        motifyItem.addActionListener(flavorListener);

        //调用该方法即可设置右键菜单，无须使用事件机制；
        ta.setComponentPopupMenu(pop);

        //设置关闭窗口时，退出程序
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        //方法内部不再嵌套方法，而是调用语句了
    }

    private void changeFlavor(int flavor) throws Exception {
        switch (flavor) {
            //设置Metal风格
            case 1:
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                break;
            case 2:
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            case 3:
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                break;
            case 4:
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                break;
            case 5:
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                break;
        }
        //更新f窗口内顶级容器以及内部所有组件的UI
        SwingUtilities.updateComponentTreeUI(f.getContentPane());
        //更新mb菜单条以及内部所有组件的UI
        SwingUtilities.updateComponentTreeUI(mb);
        //更新pop右键菜单以及内部所有组件的UI
        SwingUtilities.updateComponentTreeUI(pop);
    }


    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
//        App app = new App();
//        app.init();
        JavaClock javaClock = new JavaClock();
        javaClock.run();
    }
}
