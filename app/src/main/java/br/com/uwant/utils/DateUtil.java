package br.com.uwant.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.uwant.R;

/**
 * Classe utilitária para ações relacionadas a entidades de data.
 */
public abstract class DateUtil {

    private static final int DEFAULT_TIME_AGO = R.string.text_feeds_just_now;
    private static final int YEARS = R.plurals.text_feeds_years;
    private static final int MONTHS = R.plurals.text_feeds_months;
    private static final int DAYS = R.plurals.text_feeds_days;
    private static final int HOURS = R.plurals.text_feeds_hours;
    private static final int MINUTES = R.plurals.text_feeds_minutes;

    /**
     * Padrão de data brasileira.
     */
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    /**
     * Padrão de hora brasileira.
     */
    public static final String HOUR_PATTERN = "HH:mm:ss";

    /**
     * Padrão de data/hora brasileira.
     */
    public static final String DATE_HOUR_PATTERN = "dd/MM/yyyy HH:mm:ss";

    /**
     * Padrão de data/hora brasileira, sem os segundos.
     */
    public static final String DATE_HOUR_WITHOUT_SECONDS_PATTERN = "dd/MM/yyyy HH:mm";

    /**
     * Responsável por transformar uma string em padrão brasileiro para uma entidade Date.
     * @param dateHour
     * @return
     * @throws java.text.ParseException
     */
    public static Date parse(String dateHour, String pattern) throws ParseException {
        if (dateHour == null) return null;
        else return new SimpleDateFormat(pattern).parse(dateHour);
    }

    /**
     * Método responsável por ler uma classe Date e transforma-la em String no formato passado.
     * @param dateHour
     * @param pattern
     * @return formattedDate
     */
    public static String format(Date dateHour, String pattern) {
        if (dateHour == null) return null;
        else return new SimpleDateFormat(pattern).format(dateHour);
    }

    public static DatePicker picker(Context context, DatePickerDialog.OnDateSetListener listener) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(context, listener, year, month, day);
        dpd.show();

        return dpd.getDatePicker();
    }

    public static String getTimeAgo(Context context, Date when) {
        Resources resources = context.getResources();

        String timeAgo;
        if (when != null) {
            Date now = new Date();
            long diff = now.getTime() - when.getTime();
            int diffMinutes = (int) diff / (60 * 1000);
            if (diffMinutes > 59) {
                int diffHours = diffMinutes / 60;
                if (diffHours > 23) {
                    int diffDays = diffHours / 24;
                    if (diffDays > 29) {
                        int diffMonths = diffDays / 30;
                        if (diffMonths > 11) {
                            int diffYears = diffMonths / 12;
                            timeAgo = resources.getQuantityString(YEARS, diffYears, diffYears); // FINALMENTE...
                        } else {
                            timeAgo = resources.getQuantityString(MONTHS, diffMonths, diffMonths);
                        }
                    } else {
                        timeAgo = resources.getQuantityString(DAYS, diffDays, diffDays);
                    }
                } else {
                    timeAgo = resources.getQuantityString(HOURS, diffHours, diffHours);
                }
            } else if (diffMinutes > 0) {
                timeAgo = resources.getQuantityString(MINUTES, diffMinutes, diffMinutes);
            } else {
                timeAgo = resources.getString(DEFAULT_TIME_AGO);
            }
        } else {
            timeAgo = resources.getString(DEFAULT_TIME_AGO);
        }

        return timeAgo;
    }

}
