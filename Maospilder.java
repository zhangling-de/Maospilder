/**
 * FileName: Maospilder
 * Author:   Xiao Mi
 * Date:     2019-11-05 18:27
 * Description: 对猫眼进行信息爬取
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈对猫眼进行信息爬取〉
 *
 * @author Xiao Mi
 * @create 2019-11-05
 * @since 1.0.0
 */
public class Maospilder implements Runnable{
    private static String pageUrl;
    ExecutorService fixedThreadPool;
    List<Mao> lists;

    public Maospilder(String pageUrl,List<Mao>lists,ExecutorService fixTreadPool){
        this.pageUrl = pageUrl;
        this.lists = lists;
        this.fixedThreadPool = fixTreadPool;
    }

    public void run(){
        if(pageUrl.endsWith("4")){//https://maoyan.com/board/4,TOP100榜
            for(int i = 0;i<3;i++){
                String request = pageUrl+"?offset="+i*10;
                fixedThreadPool.execute(new Spildermao(request));
            }
        }else if(pageUrl.endsWith("6")){//https://maoyan.com/board/6,最受期待榜
            for(int i = 0;i<5;i++){
                String request = pageUrl.replace("4","6")+"?offset="+i*10;
                fixedThreadPool.execute(new Spildermao(request));
            }
        }else if(pageUrl.endsWith("7")){//https://maoyan.com/board/7,热映口碑榜
            String request = pageUrl.replace("4","7");
            fixedThreadPool.execute(new Spildermao(request));
        }else if(pageUrl.endsWith("1")){
            String request = pageUrl.replace("4","1");//https://maoyan.com/board/1,国内票房榜
            fixedThreadPool.execute(new Spildermao(request));
        }else{
            String request = pageUrl.replace("4","2");//https://maoyan.com/board/2,北美票房榜
            fixedThreadPool.execute(new Spildermao(request));
        }
        fixedThreadPool.shutdown();
    }

    public class Spildermao implements Runnable{
        String request;
        public Spildermao(String request){
            this.request = request;
        }
        public void run(){
            try{
                Document doc = Jsoup.connect(request).get();
                Elements elements = doc.select(".board-wrapper > dd");
                for(int i = 0;i< elements.size();i++){
                    String src = elements.get(i).select(".board-img").attr("data-src");
                    String picLink = src.substring(0,src.lastIndexOf("@"));//图片链接
                    String st = elements.get(i).select(".star").text();
                    String star = st.substring(st.indexOf("：")+1,st.length());
                    String re = elements.get(i).select(".releasetime").text();
                    String releaseTime = re.substring(re.indexOf("：")+1,re.length());
                    String movie = elements.get(i).select(".name").text();
                    String movieLink = elements.get(i).select(".name > a").attr("abs:href");
                    String score = elements.get(i).select(".score").text();
                    List<String> list = getComment(movieLink);
                    String snum = list.get(0);
                    String watched = list.get(1);
                    String wish = list.get(2);
                    lists.add(new Mao(picLink,movie,releaseTime,star,movieLink,score,snum,watched,wish));
                }
                writeToExcel();
            }catch(Exception e){
                System.out.println("链接："+request+"，处理失败");
            }
        }
    }

    public void writeToExcel(){
        FileOutputStream fos;
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("zhangling");
        HSSFRow row = sheet.createRow(0);
        //设置列宽，POI中的字符宽度算法是：
        //double 宽度 = (字符个数 * (字符宽度 - 1) + 5) / (字符宽度 - 1) * 256，然后四舍五入
        sheet.setColumnWidth((short)0,(short)(20*256));
        sheet.setColumnWidth((short)1,(short)(30*256));
        sheet.setColumnWidth((short)2,(short)(30*256));
        sheet.setColumnWidth((short)3,(short)(30*256));
        sheet.setColumnWidth((short)4,(short)(30*256));
        sheet.setColumnWidth((short)5,(short)(15*256));
        sheet.setColumnWidth((short)6,(short)(15*256));
        sheet.setColumnWidth((short)7,(short)(15*256));
        sheet.setColumnWidth((short)8,(short)(15*256));
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中

        HSSFCell cell1 = row.createCell(0);
        cell1.setCellValue("picture");
        cell1.setCellStyle(style);
        HSSFCell cell2 = row.createCell(1);
        cell2.setCellValue("movie");
        cell2.setCellStyle(style);
        HSSFCell cell3 = row.createCell(2);
        cell3.setCellValue("movieLink");
        cell3.setCellStyle(style);
        HSSFCell cell4 = row.createCell(3);
        cell4.setCellValue("star");
        cell4.setCellStyle(style);
        HSSFCell cell5 = row.createCell(4);
        cell5.setCellValue("releaseTime");
        cell5.setCellStyle(style);
        HSSFCell cell6 = row.createCell(5);
        cell6.setCellValue("score");
        cell6.setCellStyle(style);
        HSSFCell cell7 = row.createCell(6);
        cell7.setCellValue("snum");
        cell7.setCellStyle(style);
        HSSFCell cell8 = row.createCell(7);
        cell8.setCellValue("watched");
        cell8.setCellStyle(style);
        HSSFCell cell9 = row.createCell(8);
        cell9.setCellValue("num");
        cell9.setCellStyle(style);

        if(!lists.isEmpty()){
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();//只能申明一次
            HSSFCellStyle style1 = wb.createCellStyle();
            style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
            style1.setWrapText(true);//内容可换行
            BufferedImage bufferImg;
            for(int i = 0;i<lists.size();i++){
                String commentUrl = lists.get(i).getMovieLink();
                try{
                    row = sheet.createRow((short)sheet.getLastRowNum()+1);
                    row.setHeight((short)(150*20));//设置行高，POI中的行高＝Excel的行高度*20

                    HSSFCell cella = row.createCell(1);
                    cella.setCellValue(lists.get(i).getMovie());
                    cella.setCellStyle(style1);

                    HSSFCell cellb = row.createCell(2);
                    cellb.setCellValue(lists.get(i).getMovieLink());
                    cellb.setCellStyle(style1);

                    HSSFCell cellc = row.createCell(3);
                    cellc.setCellValue(lists.get(i).getStar());
                    cellc.setCellStyle(style1);

                    HSSFCell celld = row.createCell(4);
                    celld.setCellValue(lists.get(i).getReleaseTime());
                    celld.setCellStyle(style1);

                    HSSFCell celle = row.createCell(5);
                    celle.setCellValue(lists.get(i).getScore());
                    celle.setCellStyle(style1);

                    HSSFCell cellg = row.createCell(6);
                    cellg.setCellValue(lists.get(i).getSnum());
                    cellg.setCellStyle(style1);

                    HSSFCell cellh = row.createCell(7);
                    cellh.setCellValue(lists.get(i).getWatched());
                    cellh.setCellStyle(style1);

                    HSSFCell celli = row.createCell(8);
                    celli.setCellValue(lists.get(i).getNum());
                    celli.setCellStyle(style1);

                    URL url = new URL(lists.get(i).getPicLink());
                    ByteArrayOutputStream oui = new ByteArrayOutputStream();
                    bufferImg = ImageIO.read(url);
                    ImageIO.write(bufferImg,"jpg",oui);
                    byte[] data = oui.toByteArray();
                    // 关于HSSFClientAnchor(dx1,dy1,dx2,dy2,col1,row1,col2,row2)
                    // dx1：起始单元格的x偏移量，
                    // dy1：起始单元格的y偏移量，
                    // dx2：终止单元格的x偏移量，
                    // dy2：终止单元格的y偏移量，（刚开始时没有设置偏移量，Excel不会得到图片）
                    // col1：起始单元格列序号，从0开始计算；
                    // row1：起始单元格行序号，从0开始计算，
                    // col2：终止单元格列序号，从0开始计算；
                    // row2：终止单元格行序号，从0开始计算
                    HSSFClientAnchor anchor = new HSSFClientAnchor(0,0,1023,255,(short)0,i+1,(short)0,i+1);
                    patriarch.createPicture(anchor,wb.addPicture(data,HSSFWorkbook.PICTURE_TYPE_JPEG));
                    fos = new FileOutputStream(new File("C:Users/Xiao Mi/Desktop/nm.xls"));
                    wb.write(fos);
                    fos.flush();
                    fos.close();
                    System.out.println("已完成："+lists.get(i).getMovieLink());

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public List<String> getComment(String movieLink){
        List<String> list = new ArrayList<>(3);
        String movieId = movieLink.substring(movieLink.lastIndexOf("/")+1,movieLink.length());
        String request = "http://m.maoyan.com/asgard/asgardapi/review/realtime/data.json?movieId="+movieId;
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(request);
        HttpResponse response = null;
        try{
            response = client.execute(httpget);
            if(response.getStatusLine().getStatusCode()==200){
                HttpEntity entity = response.getEntity();
                if(entity !=null){
                    String body = EntityUtils.toString(entity,"UTF-8");

                    JSONObject jsobject = JSON.parseObject(body);
                    JSONObject data = jsobject.getJSONObject("data");
                    String snum = data.getString("snum");
                    String watched = data.getString("watched");
                    String wish = data.getString("wish");
                    list.add(snum);
                    list.add(watched);
                    list.add(wish);
                }
            }
        }catch(Exception e){
            System.out.println("处理:"+request+"失败，返回状态码："+response.getStatusLine().getStatusCode());
        }
        return list;
    }

    public static void main(String[] args){
        String mainUrl ="https://maoyan.com/board/4";
        List<Mao> list = new ArrayList<>();
        ExecutorService fixThread = Executors.newFixedThreadPool(4);
        ExecutorService threadPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(5));
        threadPool.execute(new Maospilder(mainUrl,list,fixThread));
        threadPool.shutdown();
    }
}