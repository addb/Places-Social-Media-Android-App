package io.krumbs.sdk.starter;

/**
 * Created by ADDB Inc on 13-03-2016.
 */
public class CardContent {
    String   img_url;
    String caption;
    private String country;
    private String emoji;
    private String city;
    private Long time;


    /*public CardContent(String img_url,String caption){
        this.img_url =img_url;
        this.caption=caption;
    }*/

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public String hoursAgo() {
        int timeLapseHours = (int) (System.currentTimeMillis() - time) / (60 * 60 * 1000);
        int timeLapseDays = timeLapseHours / 24;
        int timeLapseMins = timeLapseDays * 60;
        if (timeLapseDays > 0)
            if(timeLapseDays > 1)
                return (""+timeLapseDays+" days ago");
            else
                return(""+timeLapseDays+" day ago");
        else if(timeLapseMins<60)
            if(timeLapseMins>1)
                return (""+timeLapseHours+" minutes ago");
            else
                return (""+timeLapseHours+" minute ago");
        else
            return (""+timeLapseHours+" hours ago");

    }

}
