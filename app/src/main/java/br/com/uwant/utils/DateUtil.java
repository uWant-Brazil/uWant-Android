package br.com.uwant.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Classe utilitária para ações relacionadas a entidades de data.
 */
public abstract class DateUtil {

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

}
