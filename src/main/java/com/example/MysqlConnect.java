package com.example;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class MysqlConnect {
    static Connection connect ;
    static String databaseName;
    static String url;
    static String username ;
    static String password ;
    static ResultSet rs;
    static Statement query;
    public MysqlConnect(){
        this.connect = null;
        this.databaseName = "books2";   //  改成自己的資料庫名稱
        this.url = "jdbc:mysql://localhost:3306/"+databaseName;
        this.username = "root";
        this.password = "8812271227";//  改成自己的root密碼（下載mysql的時候會設定）
    }
    public void connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(url, username, password);//建立連線
            System.out.println("連接database成功");
        }catch (ClassNotFoundException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
        }catch (SQLException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
    public void addData(String title, String author, String addTime, String content){ //新增資料
        try{
            query = connect.createStatement();
            query.executeUpdate("INSERT INTO `booksContent2` VALUES (\""+title+"\",\""+author+"\",\""+addTime+"\",\""+content+"\");");
        }catch (SQLException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    public void updateData(String title,String updateContent, String updateAddTime){
        try{
            query = connect.createStatement();
            query.executeUpdate("UPDATE booksContent2 SET 時間 = \""+updateAddTime+"\" , 內容 = \""+updateContent+"\" WHERE 書名 = \""+title+"\";");
        }catch (SQLException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
    public void editOnGui(String content, String newTitle, String newAuthor){
        try{
            query = connect.createStatement();
            query.executeUpdate("UPDATE booksContent2 SET 書名 = \""+newTitle+"\" , 作者 = \""+newAuthor+"\" WHERE 內容 = \""+content+"\";");
        }catch (SQLException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
    public Object[][] getData(){//取得所有資料
        Object[][] data = new Object[10][4];;
        try{
             Statement query2 = connect.createStatement();
             ResultSet rs2;
            rs2 = query2.executeQuery("select count( * )  from booksContent2;");
            if (rs2.next()) {
                int count = rs2.getInt(1);
            }
            int n = rs2.getInt(1);//取得有幾筆資料
            //System.out.printf("%d", n);
            data = new Object[n][4];
            query = connect.createStatement();
            rs = query.executeQuery("SELECT * FROM `booksContent2`;");
            int i=0;
            while(rs.next()){
                String title = rs.getString("書名");
                String author = rs.getString("作者");
                String addTime = rs.getString("時間");
                String content = rs.getString("內容");
                // 新增到gui頁面
                data[i++]= new Object[]{title, author, addTime, content};
            }
        }catch (SQLException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
        return data;
    }
    public  void deleteData(String title){//刪除需要由primary key(書名)做索引
        try{
            query = connect.createStatement();
            query.executeUpdate("DELETE FROM `booksContent2` WHERE `書名` = \""+title+"\";");
        }catch (SQLException e){
            Logger.getLogger(MysqlConnect.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }

    public void close() { //關閉資料庫
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
