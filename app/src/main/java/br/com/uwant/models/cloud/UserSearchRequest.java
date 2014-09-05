package br.com.uwant.models.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.models.UserSearchModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class UserSearchRequest extends AbstractRequest<List<Person>> implements IRequest<UserSearchModel, List<Person>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/search";

    @Override
    public void executeAsync(UserSearchModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<UserSearchModel> getDataClass() {
        return UserSearchModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<Person> parse(String response) {
        List<Person> persons = new ArrayList<Person>(5);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.USERS)) {
                JsonElement jsonFriends = jsonObject.get(Requester.ParameterKey.USERS);
                if (jsonFriends.isJsonArray()) {
                    JsonArray arrayFriends = jsonFriends.getAsJsonArray();
                    for (int i = 0;i < arrayFriends.size();i++) {
                        JsonElement jsonFriend = arrayFriends.get(i);
                        if (jsonFriend.isJsonObject()) {
                            JsonObject jsonFriendObj = jsonFriend.getAsJsonObject();

                            if (jsonFriendObj.has(Requester.ParameterKey.LOGIN)
                                    && jsonFriendObj.has(Requester.ParameterKey.NAME)) {
                                String login = jsonFriendObj.get(Requester.ParameterKey.LOGIN).getAsString();
                                String name = jsonFriendObj.get(Requester.ParameterKey.NAME).getAsString();

                                Person person = new Person(login, name);

                                if (jsonFriendObj.has(Requester.ParameterKey.PICTURE)) {
                                    JsonElement jsonElementPicture = jsonFriendObj.get(Requester.ParameterKey.PICTURE);
                                    if (!jsonElementPicture.isJsonNull()) {
                                        JsonObject jsonPicture = jsonElementPicture.getAsJsonObject();
                                        if (jsonPicture.has(Requester.ParameterKey.URL)) {
                                            String url = jsonPicture.get(Requester.ParameterKey.URL).getAsString();

                                            Multimedia picture = new Multimedia();
                                            picture.setUrl(url);

                                            person.setPicture(picture);
                                        }
                                    }
                                }

                                persons.add(person);
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(persons, new Comparator<Person>() {

            @Override
            public int compare(Person person, Person person2) {
                return person.getName().compareTo(person2.getName());
            }

        });

        return persons;
    }

    @Override
    protected List<Person> debugParse() {
        int friendsSize = (int)(Math.random() * 15);
        List<Person> persons = new ArrayList<Person>(friendsSize + 5);
        for (int i = 0;i < friendsSize;i++) {
            Person person = new Person();
            person.setLogin("search.login." + i);
            person.setName("Name @ " + i);
            persons.add(person);
        }
        return persons;
    }
}
