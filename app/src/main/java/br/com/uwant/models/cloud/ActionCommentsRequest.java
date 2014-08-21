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
import br.com.uwant.models.classes.Comment;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.models.ListCommentsModel;
import br.com.uwant.models.cloud.models.WantModel;
import br.com.uwant.utils.DateUtil;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionCommentsRequest extends AbstractRequest<Action> implements IRequest<ListCommentsModel, Action> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/listComments";

    private Action mAction;

    @Override
    public void executeAsync(ListCommentsModel data, OnRequestListener listener) {
        this.mAction = data.getAction();
        execute(data, listener);
    }

    @Override
    public Class<ListCommentsModel> getDataClass() {
        return ListCommentsModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Action parse(String response) {
        List<Comment> comments = new ArrayList<Comment>(25);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.COMMENTS)) {
                JsonElement jsonComments = jsonObject.get(Requester.ParameterKey.COMMENTS);
                if (jsonComments.isJsonArray()) {
                    JsonArray arrayComments = jsonComments.getAsJsonArray();
                    for (int i = 0; i < arrayComments.size(); i++) {
                        JsonElement jsonComment = arrayComments.get(i);
                        if (jsonComment.isJsonObject()) {
                            JsonObject jsonCommentObj = jsonComment.getAsJsonObject();

                            if (jsonCommentObj.has(Requester.ParameterKey.USER)) {
                                JsonElement jsonElementPerson = jsonCommentObj.get(Requester.ParameterKey.USER);
                                if (jsonElementPerson.isJsonObject()) {
                                    JsonObject jsonFriendObj = jsonElementPerson.getAsJsonObject();

                                    if (jsonFriendObj.has(Requester.ParameterKey.LOGIN)
                                            && jsonFriendObj.has(Requester.ParameterKey.NAME)) {
                                        String login = jsonFriendObj.get(Requester.ParameterKey.LOGIN).getAsString();
                                        String name = jsonFriendObj.get(Requester.ParameterKey.NAME).getAsString();

                                        Person who = new Person(login, name);

                                        if (jsonFriendObj.has(Requester.ParameterKey.PICTURE)) {
                                            JsonElement jsonElementPicture = jsonFriendObj.get(Requester.ParameterKey.PICTURE);
                                            if (!jsonElementPicture.isJsonNull()) {
                                                JsonObject jsonPicture = jsonElementPicture.getAsJsonObject();
                                                if (jsonPicture.has(Requester.ParameterKey.URL)) {
                                                    String url = jsonPicture.get(Requester.ParameterKey.URL).getAsString();

                                                    Multimedia picture = new Multimedia();
                                                    picture.setUrl(url);

                                                    who.setPicture(picture);
                                                }
                                            }
                                        }

                                        if (jsonCommentObj.has(Requester.ParameterKey.ID)
                                                && jsonCommentObj.has(Requester.ParameterKey.TEXT)
                                                && jsonCommentObj.has(Requester.ParameterKey.SINCE)) {
                                            long id = jsonCommentObj.get(Requester.ParameterKey.ID).getAsLong();
                                            String text = jsonCommentObj.get(Requester.ParameterKey.TEXT).getAsString();
                                            String sinceStr = jsonCommentObj.get(Requester.ParameterKey.SINCE).getAsString();
                                            Date since;
                                            try {
                                                since = DateUtil.parse(sinceStr, DateUtil.DATE_HOUR_PATTERN);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                since = new Date();
                                            }

                                            Comment comment = new Comment();
                                            comment.setId(id);
                                            comment.setText(text);
                                            comment.setWho(who);
                                            comment.setSince(since);

                                            comments.add(comment);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        this.mAction.setComments(comments);
        return this.mAction;
    }

    @Override
    protected Action debugParse() {
        List<Comment> comments = new ArrayList<Comment>();
        for (int i = 0;i < this.mAction.getCommentsCount();i++) {
            Person who = new Person();
            who.setLogin("who.login." + i);
            who.setName("Name # " + i);

            Calendar since = Calendar.getInstance();
            since.add(Calendar.MINUTE, (-1) * (i + 1));

            Comment comment = new Comment();
            comment.setId(i);
            comment.setText("Text @ " + i);
            comment.setWho(who);
            comment.setSince(since.getTime());
            comments.add(comment);
        }

        this.mAction.setComments(comments);
        return this.mAction;
    }
}
