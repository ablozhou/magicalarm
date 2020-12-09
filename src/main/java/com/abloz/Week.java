package com.abloz;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class Week  {
    Locale locale = Locale.getDefault();
    DayOfWeek day;
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public Week(DayOfWeek day){
        this.day = day;
    }
    @Override
    public String toString() {
        return day.getDisplayName(TextStyle.FULL, locale);
    }

    Integer getValue(){
        return day.getValue();
    }

    Integer getQuarzValue(){
        if(day.getValue() == 6) return 1;
        return day.getValue()+2;
    }

    /**
     *
     * @param d day of week. 1-7 means mondy-sumday
     */
    void setValue(Integer d){
        day = DayOfWeek.of(d);
    }

}
