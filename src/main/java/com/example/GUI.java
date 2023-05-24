package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.FlowLayout;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;



public class GUI {
    private JFrame frame;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private boolean isEditable = false;
    private JButton editButton;
    private JTextField searchField = new JTextField();
    MysqlConnect database = new MysqlConnect(); //建立操作資料庫的變數
    int addNum = 1;

    public GUI() {
        frame = new JFrame("BookHive");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(new Color(255, 215, 0));

        database.connect();//建立連線
        String[] columnNames = {"書名", "作者", "新增/編輯時間", "內容"};
        Object[][] data = new Object[0][columnNames.length];
        tableModel = new DefaultTableModel(data, columnNames);
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //取得資料庫的資料
        Object[][] allData = database.getData();
        int rows = allData.length;
        for(int i =0;i < rows; i++){//object大小
            Object[] rowData = {allData[i][0], allData[i][1], allData[i][2], allData[i][3]};
            tableModel.addRow(rowData);
        }


        bookTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return isEditable && column != 3 && column != 2;
            }
        };

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(bookTable.getModel());
        bookTable.setRowSorter(sorter);

        // 按標題排序
        for (int i = 0; i < columnNames.length; i++) {
            final int columnIndex = i;
            bookTable.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int clickedColumnIndex = bookTable.getTableHeader().columnAtPoint(e.getPoint());
                    sortColumn(clickedColumnIndex);
                }
            });
        }

        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = bookTable.getSelectedRow();
                    if (selectedIndex != -1) {
                        // 取得書籍資訊
                        String title = (String) tableModel.getValueAt(selectedIndex, 0);
                        String author = (String) tableModel.getValueAt(selectedIndex, 1);
                        String content = (String) tableModel.getValueAt(selectedIndex, 3);

                        // 打開新視窗看書
                        JFrame bookFrame = new JFrame();
                        bookFrame.setBackground(new Color(255, 215, 0));
                        JTextArea contentArea = new JTextArea();
                        //edit by jyun 70~72
                        contentArea.setLineWrap(true);
                        //
                        contentArea.setEditable(isEditable);
                        contentArea.setText(content);
                        JScrollPane scrollPane = new JScrollPane(contentArea);
                        bookFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
                        bookFrame.setTitle(title + " - " + author);
                        bookFrame.setSize(600, 400);
                        bookFrame.setVisible(true);

                        final JTextArea finalContentArea = contentArea;
                        final int finalSelectedIndex = selectedIndex;
                        final JFrame finalBookFrame = bookFrame;

                        if (isEditable) {
                            JButton saveButton = new JButton("保存");
                            saveButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // 獲取新内容保存時間
                                    String updatedContent = finalContentArea.getText();
                                    String updatedAddTime = tableModel.getValueAt(finalSelectedIndex, 2).toString();

                                    // 更新Table
                                    tableModel.setValueAt(updatedContent, finalSelectedIndex, 3);
                                    tableModel.setValueAt(updatedAddTime, finalSelectedIndex, 2);

                                    //更新資料庫
                                    Object bookTitle = tableModel.getValueAt(finalSelectedIndex, 0);
                                    String title = String.valueOf(bookTitle);
                                    database.updateData(title, updatedContent, updatedAddTime);

                                    JOptionPane.showMessageDialog(finalBookFrame, "保存成功");
                                }
                            });

                            JPanel buttonPanel = new JPanel();
                            buttonPanel.add(saveButton);
                            bookFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                        }
                    }
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(bookTable);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        //新增書籍

        JButton addButton = new JButton("新增書籍");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddBookDialog dialog = new AddBookDialog(frame);
                dialog.setVisible(true);
                int n = tableModel.getRowCount();
//                System.out.printf("%d", book.getTitle().length());
//                System.out.printf("%s\n", (String) tableModel.getValueAt(1, 0));
                if (dialog.getBook() != null) {
                    // 將新增的書籍加入到TableModel中
                    Book book = dialog.getBook();
                    //書名重複
                    System.out.printf("%s\n", book.getTitle());
                    addNum = 0;
                    for(int i=0; i<n ; i++){
//                        if(book.getTitle().startsWith((String) tableModel.getValueAt(i, 0))&& book.getTitle().length() ==  ((String) tableModel.getValueAt(i, 0)).length()){
//                            addNum++;
//                        }

                        String s = (String) tableModel.getValueAt(i, 0);
                        int endIndex = s.indexOf("(");
                        if(endIndex != -1) s = s.substring(0, endIndex);
                        System.out.printf("%d", s.length());

                        if(book.getTitle().equals(s)){
                            addNum++;
                        }
                    }
                    if(addNum != 0) {
                        String newTitle = book.getTitle() + new String("(" + addNum + ")");
                        book.setTitle(newTitle);
                    }

                    Object[] rowData = {book.getTitle(), book.getAuthor(), book.getAddTime(), book.getContent()};
                    //加入資料庫
                    database.addData(book.getTitle(), book.getAuthor(), book.getAddTime(), book.getContent());
                    //加入表格
                    tableModel.addRow(rowData);
                }
            }
        });

        //刪除書籍
        JButton deleteButton = new JButton("刪除書籍");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = bookTable.getSelectedRow();
                System.out.printf("%d", selectedIndex);
                if (selectedIndex != -1) {
                    int result = JOptionPane.showConfirmDialog(frame, "確定要刪除此書嗎？", "確認刪除", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        // 從TableModel中刪除選中的書籍
                        String bookTitle = (String) tableModel.getValueAt(selectedIndex, 0);
                        //System.out.printf("%s", title);
                        tableModel.removeRow(selectedIndex);
                        //在資料庫裡刪除
                        database.deleteData(bookTitle);
                        searchBooks(searchField.getText());
                    }
                }
            }
        });

        // 編輯功能
        editButton = new JButton("編輯");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTableEditable(); // 更新表格的可編輯性
                editButton.setText(isEditable ? "完成編輯" : "編輯"); // 更新按鈕文字
            }
        });

        // 搜尋功能
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        JLabel searchLabel = new JLabel(" 依照書名搜尋 ：");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        frame.getContentPane().add(searchPanel, BorderLayout.NORTH);
        // 關鍵字輸入監聽器
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchBooks(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchBooks(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchBooks(searchField.getText());
            }
        });


        // 底下按鈕欄
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    // 排序方法
    private void sortColumn(int columnIndex) {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) bookTable.getRowSorter();
        List<? extends SortKey> sortKeys = sorter.getSortKeys();
        SortOrder sortOrder = SortOrder.ASCENDING;

        if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() == columnIndex) {
            SortOrder currentSortOrder = sortKeys.get(0).getSortOrder();
            sortOrder = (currentSortOrder == SortOrder.ASCENDING) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        }

        List<SortKey> newSortKeys = new ArrayList<>();
        // 刪除原有的排序鍵
        sorter.setSortKeys(null);

        // 添加新的排序鍵
        newSortKeys.add(new SortKey(columnIndex, sortOrder));
        sorter.setSortKeys(newSortKeys);
        sorter.sort();
    }

    private void updateTableEditable() {
        isEditable = !isEditable; // 切換編輯狀態
        bookTable.getTableHeader().repaint(); // 重繪表格標題
        bookTable.repaint(); // 重繪表格
    }

    private void searchBooks(String keyword) {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) bookTable.getRowSorter();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        List<Term> terms = HanLP.segment(keyword);
        for (Term term : terms) {
            filters.add(RowFilter.regexFilter("(?i).*" + term.word + ".*", 0)); // 0 表示書名欄位的索引
        }

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }



}