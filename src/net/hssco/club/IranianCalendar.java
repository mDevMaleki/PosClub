package net.hssco.club;

import java.util.GregorianCalendar;

public class IranianCalendar {

    private int irYear;
    private int irMonth;
    private int irDay;

    public IranianCalendar() {
        GregorianCalendar gc = new GregorianCalendar();
        int gy = gc.get(GregorianCalendar.YEAR);
        int gm = gc.get(GregorianCalendar.MONTH) + 1;
        int gd = gc.get(GregorianCalendar.DAY_OF_MONTH);

        calcSolarCalendar(gy, gm, gd);
    }

    public int getIranianYear() { return irYear; }
    public int getIranianMonth() { return irMonth; }
    public int getIranianDay() { return irDay; }

    private void calcSolarCalendar(int gy, int gm, int gd) {
        int[] gdm = {0,31,59,90,120,151,181,212,243,273,304,334};
        int jy;

        int gy2 = (gm > 2) ? (gy + 1) : gy;

        long days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
                ((gy2 + 399) / 400) + gd + gdm[gm - 1];

        jy = -1595 + (int)(33 * (days / 12053));
        days %= 12053;
        jy += 4 * (days / 1461);
        days %= 1461;

        if (days > 365) {
            jy += (days - 1) / 365;
            days = (days - 1) % 365;
        }

        int jm, jd;
        if (days < 186) {
            jm = 1 + (int)(days / 31);
            jd = 1 + (int)(days % 31);
        } else {
            days -= 186;
            jm = 7 + (int)(days / 30);
            jd = 1 + (int)(days % 30);
        }


        irYear = jy;
        irMonth = jm;
        irDay = jd;
    }
}
