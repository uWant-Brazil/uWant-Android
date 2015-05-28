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
import br.com.uwant.models.cloud.models.FriendsCircleModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class FriendsCircleRequest extends AbstractRequest<List<Person>> implements IRequest<FriendsCircleModel, List<Person>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/circle/list";

    @Override
    public void executeAsync(FriendsCircleModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<FriendsCircleModel> getDataClass() {
        return FriendsCircleModel.class;
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
            if (jsonObject.has(Requester.ParameterKey.FRIENDS)) {
                JsonElement jsonFriends = jsonObject.get(Requester.ParameterKey.FRIENDS);
                if (jsonFriends.isJsonArray()) {
                    JsonArray arrayFriends = jsonFriends.getAsJsonArray();
                    for (int i = 0;i < arrayFriends.size();i++) {
                        JsonElement jsonFriend = arrayFriends.get(i);
                        if (jsonFriend.isJsonObject()) {
                            JsonObject jsonFriendObj = jsonFriend.getAsJsonObject();

                            if (jsonFriendObj.has(Requester.ParameterKey.LOGIN)
                                    && jsonFriendObj.has(Requester.ParameterKey.NAME)
                                    && jsonFriendObj.has(Requester.ParameterKey.ID)) {
                                long id = jsonFriendObj.get(Requester.ParameterKey.ID).getAsLong();
                                String login = jsonFriendObj.get(Requester.ParameterKey.LOGIN).getAsString();
                                String name = jsonFriendObj.get(Requester.ParameterKey.NAME).getAsString();
                                String mail = jsonFriendObj.get(Requester.ParameterKey.MAIL).getAsString();

                                Person person = new Person(id, login, name);
                                person.setMail(mail);
                                person.setFriendshipLevel(Person.FriendshipLevel.MUTUAL);

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
            String login = "person_" + i;
            String name = "Person#" + i;
            Person person = new Person(i, login, name);
            person.setFriendshipLevel(Person.FriendshipLevel.MUTUAL);

            if (i % 3 == 0) {
                Multimedia picture = new Multimedia();
                picture.setUrl("http://dailysignal.com/wp-content/uploads/armstrong.jpg");
                person.setPicture(picture);
            }

            persons.add(person);
        }

        return persons;
    }
}
