package com.example;
import java.io.IOException;
import java.util.regex.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class crawler {
    private String url;
    private String Title;
    // private String url="http://big5.quanben5.com/n/doushentianxia/13685.html";
    // private String url="http://big5.quanben5.com/n/yidaoguantu/25859.html";
    // private String url="http://big5.quanben5.com/n/zhongshengzuiqiangnongmin/63297.html";
    public String getArticle() throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div#content p"); //選擇標籤<div>id:content下的<p>
            Elements titleElement=doc.select("div.topbar div.row span");
            Title= titleElement.text();//轉型態
            String combinedText = "";//內文
            for (Element element : elements) {
                combinedText += element.text() + "\n"; //把各個p換行之後加到combinedText
            }
            return String.format("%s",combinedText);
        } catch (Exception e) {
            throw e;
        }
    }
    public String getTitle(){
        return this.Title;
    }
    public void setUrl(String a) throws ArithmeticException{
        Pattern pattern = Pattern.compile("^http:\\/\\/big5\\.quanben5\\.com\\/n\\/\\S+\\/\\d+\\.html$");
        Matcher matcher = pattern.matcher(a);
        if (matcher.matches()) {
            this.url=a;
        }
        else{
            int err=5/0;
        }
        /*if (a.startsWith("http://big5.quanben5.com/")) {
            this.url=a;
        }
        else{
            int err=5/0;
        }*/
    }   

}
