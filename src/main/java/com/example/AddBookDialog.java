package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.text.SimpleDateFormat;


public class AddBookDialog extends JDialog {
    private JTextField titleField;
    private JTextArea contentArea;
    private JRadioButton urlButton;
    private JRadioButton manualButton;
    private JTextField urlField;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField authorField;

    private FocusListener titleFieldFocusListener;
    private FocusListener authorFieldFocusListener;


    private Book book;

    public AddBookDialog(JFrame parentFrame) {
        super(parentFrame, "新增書籍", true);
        this.setBackground(new Color(255, 215, 0));

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 1));

        titleField = new JTextField();
        inputPanel.add(new JLabel("標題："));
        inputPanel.add(titleField);

        authorField = new JTextField();
        inputPanel.add(new JLabel("作者姓名："));
        inputPanel.add(authorField);


        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        inputPanel.add(new JLabel("內容："));
        inputPanel.add(new JScrollPane(contentArea));

        urlButton = new JRadioButton("網址爬蟲", false);
        manualButton = new JRadioButton("手動新增", false);

        ButtonGroup sourceGroup = new ButtonGroup();
        sourceGroup.add(urlButton);
        sourceGroup.add(manualButton);

        JPanel sourcePanel = new JPanel(new GridLayout(1, 0));
        sourcePanel.add(urlButton);
        sourcePanel.add(manualButton);
        inputPanel.add(new JLabel("書籍來源："));
        inputPanel.add(sourcePanel);

        urlField = new JTextField("網址列");
        urlField.setForeground(Color.GRAY);
        inputPanel.add(urlField);
        urlField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (urlField.getText().equals("網址列")) {
                    urlField.setText("");
                    urlField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (urlField.getText().isEmpty()) {
                    urlField.setText("網址列");
                    urlField.setForeground(Color.GRAY);
                }
            }
        });

        titleFieldFocusListener = new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
                if (titleField.getText().equals("爬蟲會提取，可自定義")) {
                    titleField.setText("");
                    titleField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (titleField.getText().isEmpty()) {
                    titleField.setText("爬蟲會提取，可自定義");
                    titleField.setForeground(Color.GRAY);
                }
            }
        };

        authorFieldFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (authorField.getText().equals("網路小說，可自定義")) {
                    authorField.setText("");
                    authorField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (authorField.getText().isEmpty()) {
                    authorField.setText("網路小說，可自定義");
                    authorField.setForeground(Color.GRAY);
                }
            }
        };

        urlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                urlField.setEnabled(true);
                contentArea.setFocusable(false);
                if (titleField.getText().isEmpty()) {
                    titleField.setText("爬蟲會提取，可自定義");
                    titleField.setForeground(Color.GRAY);
                }
                if (authorField.getText().isEmpty()) {
                    authorField.setText("網路小說，可自定義");
                    authorField.setForeground(Color.GRAY);
                }

                titleField.addFocusListener(titleFieldFocusListener);
                authorField.addFocusListener(authorFieldFocusListener);
            }
        });

        manualButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                titleField.removeFocusListener(titleFieldFocusListener);
                authorField.removeFocusListener(authorFieldFocusListener);

                urlField.setEnabled(false);
                contentArea.setFocusable(true);
                if (titleField.getText().equals("爬蟲會提取，可自定義")) {
                    titleField.setText("");
                    titleField.setForeground(Color.BLACK);
                }
                if (authorField.getText().equals("網路小說，可自定義")) {
                    authorField.setText("");
                    authorField.setForeground(Color.BLACK);
                }
            }
        });

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        saveButton = new JButton("儲存");
        saveButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = contentArea.getText();
                String author = authorField.getText();

                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String addTime = dateFormat.format(currentDate);

                if(!urlButton.isSelected() && !manualButton.isSelected()){
                    JOptionPane.showMessageDialog(AddBookDialog.this, "請先選擇書籍來源。");
                    return;
                } else if (urlButton.isSelected()) {
                    if (title.isEmpty()) {
                        JOptionPane.showMessageDialog(AddBookDialog.this, "請輸入書籍標題。");
                        return;
                    }
                    String url = urlField.getText();
                    if (url.equals("網址列")) {
                        JOptionPane.showMessageDialog(AddBookDialog.this, "請輸入書籍網址。");
                        return;
                    }

                    try {
                        crawler demo = new crawler();
                        demo.setUrl(url);
                        String html = demo.getArticle();
                        if(title.equals("爬蟲會提取，可自定義")) title = demo.getTitle();
                        if (author.equals("網路小說，可自定義")) book = new Book(title, "網路小說", html, addTime);
                        else book = new Book(title, author, html, addTime);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AddBookDialog.this, "無法獲取書籍內容，請檢查網址。");
                        return;
                    }
                } else {
                    if (title.isEmpty() || content.isEmpty()) {
                        JOptionPane.showMessageDialog(AddBookDialog.this, "請輸入書籍標題和內容。");
                        return;
                    }
                    if (author.isEmpty()) book = new Book(title, "", content, addTime);
                    else book = new Book(title, author, content, addTime);
                }

                AddBookDialog.this.dispose();
            }
        });

        cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddBookDialog.this.dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        pack();
        setLocationRelativeTo(parentFrame);
    }
    public Book getBook() {
        return book;
    }
}