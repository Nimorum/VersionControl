package org.VVC;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View implements ActionListener {
    JFrame frame=new JFrame("VVC");
    Container mainContainer;
    JPanel topPanel=new JPanel();
    JPanel leftPanel=new JPanel();
    JPanel centerPanel=new JPanel();

    JLabel commitMessage=new JLabel("Commit");

    private JTextArea commitMsg=new JTextArea("msg",3,50);

    public View(){
        setLayout();
    }

    private void setLayout(){
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));

        mainContainer=frame.getContentPane();
        mainContainer.setLayout(new BorderLayout());

        topPanel.setLayout(new FlowLayout(10));
        topPanel.setBorder(new LineBorder(Color.BLACK,2));
        topPanel.setVisible(true);
        setupTopPanel();

        centerPanel.setLayout(new GridLayout(8,1,10,10));
        centerPanel.setBorder(new LineBorder(Color.BLACK,2));
        centerPanel.setVisible(true);
        setupCenterPanel();

        leftPanel.setLayout(new GridLayout(15,1,10,10));
        leftPanel.setBorder(new LineBorder(Color.BLACK,2));
        leftPanel.setVisible(true);
        setupLeftPanel();

        mainContainer.add(topPanel,BorderLayout.NORTH);
        mainContainer.add(leftPanel,BorderLayout.WEST);
        mainContainer.add(centerPanel,BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void setupTopPanel(){
        JButton installBnt=new JButton("Install");
        JButton files=new JButton("Files");
        JButton chatBnt=new JButton("chat");
        topPanel.add(installBnt);
        topPanel.add(files);
        topPanel.add(chatBnt);
        installBnt.addActionListener(this);
        files.addActionListener(this);
        chatBnt.addActionListener(this);
    }

    private void setupLeftPanel(){
        JButton fetchBnt=new JButton("Fetch");
        fetchBnt.addActionListener(this);
        leftPanel.add(fetchBnt);
    }

    private void setupCenterPanel(){
        JPanel row=new JPanel();
        row.add(commitMessage);
        row.add(commitMsg);

        centerPanel.add(row);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Install")){
            System.out.println("installing");
        }
        if(e.getActionCommand().equals("update")){
            System.out.println("updateClick");
        }
        if(e.getActionCommand().equals("Commit")){
            System.out.println("commitClick");
        }
        if(e.getActionCommand().equals("Fetch")){
            System.out.println("fetchClick");
        }
    }

}
