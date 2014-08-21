package br.com.uwant.models.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.ActionsModel;
import br.com.uwant.models.cloud.models.AuthModel;
import br.com.uwant.utils.DateUtil;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionsRequest extends AbstractRequest<List<Action>> implements IRequest<ActionsModel, List<Action>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/notification/list";

    @Override
    public void executeAsync(ActionsModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ActionsModel> getDataClass() {
        return ActionsModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<Action> parse(String response) {
        List<Action> actions = new ArrayList<Action>();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.ACTIONS)) {
                JsonElement jsonActions = jsonObject.get(Requester.ParameterKey.ACTIONS);
                if (jsonActions.isJsonArray()) {
                    JsonArray arrayActions = jsonActions.getAsJsonArray();
                    for (int i = 0; i < arrayActions.size(); i++) {
                        JsonElement jsonAction = arrayActions.get(i);
                        if (jsonAction.isJsonObject()) {
                            JsonObject jsonActionObj = jsonAction.getAsJsonObject();

                            if (jsonActionObj.has(Requester.ParameterKey.TYPE)
                                    && jsonActionObj.has(Requester.ParameterKey.WHEN)
                                    && jsonActionObj.has(Requester.ParameterKey.MESSAGE)) {
                                int typeOrdinal = jsonActionObj.get(Requester.ParameterKey.TYPE).getAsInt();
                                Action.Type type = Action.Type.values()[typeOrdinal];

                                String message = jsonActionObj.get(Requester.ParameterKey.MESSAGE).getAsString();

                                Date when = null;
                                String dateHour = jsonActionObj.get(Requester.ParameterKey.WHEN).getAsString();
                                try {
                                    when = DateUtil.parse(dateHour, DateUtil.DATE_HOUR_PATTERN);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    continue;
                                }

                                Action action = new Action();
                                action.setType(type);
                                action.setMessage(message);
                                action.setWhen(when);

                                long id = -1;
                                if (jsonActionObj.has(Requester.ParameterKey.ID)) {
                                    id = jsonActionObj.get(Requester.ParameterKey.ID).getAsLong();
                                }
                                action.setId(id);

                                String extra;
                                if (jsonActionObj.has(Requester.ParameterKey.EXTRA)) {
                                    extra = jsonActionObj.get(Requester.ParameterKey.EXTRA).getAsString();
                                    action.setExtra(extra);
                                }

                                Person person = null;
                                if (jsonActionObj.has(Requester.ParameterKey.USER_FROM)) {
                                    person = new Person();

                                    JsonObject jsonUser = jsonActionObj.getAsJsonObject(Requester.ParameterKey.USER_FROM);

                                    if (jsonUser.has(Requester.ParameterKey.LOGIN)) {
                                        String login = jsonUser.get(Requester.ParameterKey.LOGIN).getAsString();
                                        person.setLogin(login);
                                    }

                                    if (jsonUser.has(Requester.ParameterKey.NAME)) {
                                        String name = jsonUser.get(Requester.ParameterKey.NAME).getAsString();
                                        person.setName(name);
                                    }

                                    if (jsonUser.has(Requester.ParameterKey.PICTURE)) {
                                        JsonElement jsonElementPicture = jsonUser.get(Requester.ParameterKey.PICTURE);
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
                                    
                                    action.setFrom(person);
                                }

                                actions.add(action);
                            }
                        }
                    }
                }
            }
        }

        return actions;
    }

    @Override
    protected List<Action> debugParse() {
        int actionsSize = (int)(Math.random() * 15);
        List<Action> actions = new ArrayList<Action>(actionsSize + 5);
        for (int i = 0;i < actionsSize;i++) {
            Action.Type[] types = Action.Type.values();
            Action.Type type = types[i % types.length];

            String uuid = UUID.randomUUID().toString();
            String message = "Message # " + uuid;
            String extra = "Extra # " + uuid;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, (-1) * i);
            Date when = calendar.getTime();

            Action action = new Action();
            action.setType(type);
            action.setMessage(message);
            action.setWhen(when);
            action.setExtra(extra);

            Person person = null;
            person.setLogin("person_" + i);
            person.setName("Person#" + i);
            if (i % 3 == 0) {
                Multimedia picture = new Multimedia();
                picture.setUrl("http://dailysignal.com/wp-content/uploads/armstrong.jpg");
                person.setPicture(picture);
            }
            action.setFrom(person);

            actions.add(action);
        }
        return actions;
    }
}
