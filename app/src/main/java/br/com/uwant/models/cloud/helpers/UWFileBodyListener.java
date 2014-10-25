package br.com.uwant.models.cloud.helpers;

/**
 * Created by bonezi on 20/09/2014.
 */
public interface UWFileBodyListener {
    void preWrite(int totalAmount);
    void written(int totalBytes, int amountOfBytes);
}
