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

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.models.FeedsModel;
import br.com.uwant.utils.DateUtil;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class FeedsRequest extends AbstractRequest<List<Action>> implements IRequest<FeedsModel, List<Action>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/feeds";

    @Override
    public void executeAsync(FeedsModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<FeedsModel> getDataClass() {
        return FeedsModel.class;
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

                                Date when;
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

                                if (jsonActionObj.has(Requester.ParameterKey.WANT)) {
                                    JsonElement jsonWant = jsonActionObj.get(Requester.ParameterKey.WANT);
                                    JsonObject jsonWantObj = jsonWant.getAsJsonObject();

                                    if (jsonWantObj.has(Requester.ParameterKey.COUNT)) {
                                        int uwantsCount = jsonWantObj.get(Requester.ParameterKey.COUNT).getAsInt();
                                        action.setUWantsCount(uwantsCount);
                                    }

                                    boolean uWant = false;
                                    if (jsonWantObj.has(Requester.ParameterKey.UWANT)) {
                                        uWant = jsonWantObj.get(Requester.ParameterKey.UWANT).getAsBoolean();
                                    }
                                    action.setuWant(uWant);
                                }

                                if (jsonActionObj.has(Requester.ParameterKey.COMMENTS_COUNT)) {
                                    int commentsCount = jsonActionObj.get(Requester.ParameterKey.COMMENTS_COUNT).getAsInt();
                                    action.setCommentsCount(commentsCount);
                                }

                                if (jsonActionObj.has(Requester.ParameterKey.SHARE)) {
                                    JsonElement jsonWant = jsonActionObj.get(Requester.ParameterKey.SHARE);
                                    JsonObject jsonShareObj = jsonWant.getAsJsonObject();

                                    if (jsonShareObj.has(Requester.ParameterKey.COUNT)) {
                                        int shareCount = jsonShareObj.get(Requester.ParameterKey.COUNT).getAsInt();
                                        action.setSharesCount(shareCount);
                                    }

                                    boolean uShare = false;
                                    if (jsonShareObj.has(Requester.ParameterKey.USHARE)) {
                                        uShare = jsonShareObj.get(Requester.ParameterKey.USHARE).getAsBoolean();
                                    }
                                    action.setuShare(uShare);
                                }

                                String extra;
                                if (jsonActionObj.has(Requester.ParameterKey.EXTRA)) {
                                    extra = jsonActionObj.get(Requester.ParameterKey.EXTRA).getAsString();
                                    action.setExtra(extra);
                                }

                                if (jsonActionObj.has(Requester.ParameterKey.USER_FROM)) {
                                    Person person = new Person();

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
        int actionsSize = (int)(Math.random() * 25) + 1;
        List<Action> actions = new ArrayList<Action>(actionsSize + 5);
        for (int i = 0;i < actionsSize;i++) {
            Person from = new Person();
            from.setLogin("from.login." + i);
            from.setName("Name # " + i);

            Calendar when = Calendar.getInstance();
            when.add(Calendar.MINUTE, (-1) * ((i * 3) + 1));

            Action action = new Action();
            action.setId(i);
            action.setType(Action.Type.ACTIVITY);
            action.setuWant(i % 3 == 0);
            action.setUWantsCount(i % 6 == 0 ? i + 1 : i - 1);
            action.setSharesCount(i % 5 == 0 ? i + 1 : i - 1);
            action.setExtra("Text Extra # " + i);
            action.setCommentsCount(i % 5 == 0 ? i + 1 : i - 1);
            action.setFrom(from);
            action.setMessage("Message @ " + i);
            action.setWhen(when.getTime());
            actions.add(action);
        }
        return actions;
    }
}
