/**
 * FileName: Mao
 * Author:   Xiao Mi
 * Date:     2019-11-05 18:07
 * Description: 属性的封装
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhang;

/**
 * 〈一句话功能简述〉<br> 
 * 〈属性的封装〉
 *
 * @author Xiao Mi
 * @create 2019-11-05
 * @since 1.0.0
 */
public class Mao {
    private String picLink;//电影图片链接
    private String movie;//电影名
    private String releaseTime;//上映时间
    private String star;//参演人员
    private String movieLink;//链接
    private String score;//电影评分
    private String snum;
    private String watched;
    private String num;
    public Mao(String picLink,String movie,String releaseTime,String star,String movieLink,String score,String snum,String watched,String num){
        this.picLink = picLink;
        this.movie = movie;
        this.releaseTime = releaseTime;
        this.star = star;
        this.movieLink = movieLink;
        this.score = score;
        this.snum = snum;
        this.watched = watched;
        this.num = num;
    }
    public String getPicLink(){
        return picLink;
    }
    public String getMovie(){
        return movie;
    }
    public String getReleaseTime(){
        return releaseTime;
    }
    public String getStar(){
        return star;
    }
    public String getMovieLink(){
        return movieLink;
    }
    public String getScore(){
        return score;
    }
    public String getSnum(){
        return snum;
    }
    public String getWatched(){
        return watched;
    }
    public String getNum(){
        return num;
    }
}