package br.com.uwant.models.Observes;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Cleibson Gomes da Silva on 19/10/2014.
 */
public class TriggerCollection<T> extends Observable{

    private List<T> list = new LinkedList<T>();

    public void startTrigger(){
        setChanged();
        notifyObservers();
    }

    public void add(T element){
        list.add(element);
    }

    public void remove(T element){
        list.remove(element);
    }

}
