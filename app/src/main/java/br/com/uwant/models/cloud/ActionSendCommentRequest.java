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
import br.com.uwant.models.cloud.models.CommentModel;
import br.com.uwant.utils.DateUtil;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionSendCommentRequest extends AbstractRequest<Action> implements IRequest<CommentModel, Action> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/comment";

    private Action mAction;

    @Override
    public void executeAsync(CommentModel data, OnRequestListener listener) {
        this.mAction = data.getAction();
        execute(data, listener);
    }

    @Override
    public Class<CommentModel> getDataClass() {
        return CommentModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Action parse(String response) {
        List<Comment> comments = new ArrayList<Comment>(50);

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
                                            && jsonFriendObj.has(Requester.ParameterKey.NAME)
                                            && jsonFriendObj.has(Requester.ParameterKey.ID)) {
                                        long userId = jsonFriendObj.get(Requester.ParameterKey.ID).getAsLong();
                                        String login = jsonFriendObj.get(Requester.ParameterKey.LOGIN).getAsString();
                                        String name = jsonFriendObj.get(Requester.ParameterKey.NAME).getAsString();

                                        Person who = new Person(userId, login, name);

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

                    this.mAction.setComments(comments);
                }
            }

            if (jsonObject.has(Requester.ParameterKey.ACTION)) {
                JsonElement jsonComments = jsonObject.get(Requester.ParameterKey.ACTION);
                if (jsonComments.isJsonObject()) {
                    JsonObject jsonActionObj = jsonComments.getAsJsonObject();
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
                            return this.mAction;
                        }

                        this.mAction.setType(type);
                        this.mAction.setMessage(message);
                        this.mAction.setWhen(when);

                        long id = -1;
                        if (jsonActionObj.has(Requester.ParameterKey.ID)) {
                            id = jsonActionObj.get(Requester.ParameterKey.ID).getAsLong();
                        }
                        this.mAction.setId(id);

                        if (jsonActionObj.has(Requester.ParameterKey.WANT)) {
                            JsonElement jsonWant = jsonActionObj.get(Requester.ParameterKey.WANT);
                            JsonObject jsonWantObj = jsonWant.getAsJsonObject();

                            if (jsonWantObj.has(Requester.ParameterKey.COUNT)) {
                                int uwantsCount = jsonWantObj.get(Requester.ParameterKey.COUNT).getAsInt();
                                this.mAction.setUWantsCount(uwantsCount);
                            }

                            boolean uWant = false;
                            if (jsonWantObj.has(Requester.ParameterKey.UWANT)) {
                                uWant = jsonWantObj.get(Requester.ParameterKey.UWANT).getAsBoolean();
                            }
                            this.mAction.setuWant(uWant);
                        }

                        if (jsonActionObj.has(Requester.ParameterKey.COMMENTS_COUNT)) {
                            int commentsCount = jsonActionObj.get(Requester.ParameterKey.COMMENTS_COUNT).getAsInt();
                            this.mAction.setCommentsCount(commentsCount);
                        }

                        if (jsonActionObj.has(Requester.ParameterKey.SHARE)) {
                            JsonElement jsonWant = jsonActionObj.get(Requester.ParameterKey.SHARE);
                            JsonObject jsonShareObj = jsonWant.getAsJsonObject();

                            if (jsonShareObj.has(Requester.ParameterKey.COUNT)) {
                                int shareCount = jsonShareObj.get(Requester.ParameterKey.COUNT).getAsInt();
                                this.mAction.setSharesCount(shareCount);
                            }

                            boolean uShare = false;
                            if (jsonShareObj.has(Requester.ParameterKey.USHARE)) {
                                uShare = jsonShareObj.get(Requester.ParameterKey.USHARE).getAsBoolean();
                            }
                            this.mAction.setuShare(uShare);
                        }

                        String extra;
                        if (jsonActionObj.has(Requester.ParameterKey.EXTRA)) {
                            extra = jsonActionObj.get(Requester.ParameterKey.EXTRA).getAsString();
                            this.mAction.setExtra(extra);
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

                            this.mAction.setFrom(person);
                        }
                    }
                }
            }
        }

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
