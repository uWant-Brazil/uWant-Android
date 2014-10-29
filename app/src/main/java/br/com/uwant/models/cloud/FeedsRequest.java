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
import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
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

                                if (jsonActionObj.has(Requester.ParameterKey.WISHLIST)) {
                                    JsonElement jsonWishElem = jsonActionObj.get(Requester.ParameterKey.WISHLIST);
                                    if (jsonWishElem.isJsonObject()
                                            && !jsonWishElem.isJsonNull()) {
                                        JsonObject jsonWish = jsonWishElem.getAsJsonObject();
                                        if (jsonWish.has(Requester.ParameterKey.ID)) {
                                            List<Product> products = null;
                                            long wishListId = jsonWish.get(Requester.ParameterKey.ID).getAsLong();
                                            if (jsonWish.has(Requester.ParameterKey.PRODUCTS)) {
                                                JsonElement jsonObjElement = jsonWish.get(Requester.ParameterKey.PRODUCTS);
                                                if (jsonObjElement.isJsonArray()
                                                        && !jsonObjElement.isJsonNull()) {
                                                    JsonArray arrayProducts = jsonObjElement.getAsJsonArray();
                                                    products = new ArrayList<Product>(arrayProducts.size());
                                                    for (int k = 0; k < arrayProducts.size(); k++) {
                                                        JsonObject jsonProduct = arrayProducts.get(k).getAsJsonObject();

                                                        if (jsonProduct.has(Requester.ParameterKey.ID)
                                                                && jsonProduct.has(Requester.ParameterKey.NAME)) {
                                                            long productId = jsonProduct.get(Requester.ParameterKey.ID).getAsInt();
                                                            String name = jsonProduct.get(Requester.ParameterKey.NAME).getAsString();

                                                            Product product = new Product();
                                                            product.setId(productId);
                                                            product.setName(name);
                                                            product.setWishListId(wishListId);

                                                            if (jsonProduct.has(Requester.ParameterKey.NICK_NAME)) {
                                                                JsonElement jsonNickElem = jsonProduct.get(Requester.ParameterKey.NICK_NAME);
                                                                if (!jsonNickElem.isJsonNull()) {
                                                                    String nickName = jsonNickElem.getAsString();
                                                                    product.setNickName(nickName);
                                                                }
                                                            }

                                                            if (jsonProduct.has(Requester.ParameterKey.MULTIMEDIA)) {
                                                                JsonElement jsonPicElem = jsonProduct.get(Requester.ParameterKey.MULTIMEDIA);
                                                                if (!jsonPicElem.isJsonNull() && jsonPicElem.isJsonObject()) {
                                                                    JsonObject jsonProductPic = jsonPicElem.getAsJsonObject();
                                                                    if (jsonProductPic.has(Requester.ParameterKey.URL)) {
                                                                        String url = jsonProductPic.get(Requester.ParameterKey.URL).getAsString();

                                                                        Multimedia picture = new Multimedia();
                                                                        picture.setUrl(url);
                                                                        product.setPicture(picture);
                                                                    }
                                                                }
                                                            }

                                                            if (jsonProduct.has(Requester.ParameterKey.MANUFACTURER)) {
                                                                JsonElement jsonManuElem = jsonProduct.get(Requester.ParameterKey.MANUFACTURER);
                                                                if (!jsonManuElem.isJsonNull() && jsonManuElem.isJsonObject()) {
                                                                    JsonObject jsonManufacturer = jsonManuElem.getAsJsonObject();
                                                                    if (jsonManufacturer.has(Requester.ParameterKey.ID)
                                                                            && jsonManufacturer.has(Requester.ParameterKey.NAME)) {
                                                                        long manufacturerId = jsonManufacturer.get(Requester.ParameterKey.ID).getAsInt();
                                                                        String manufacturerName = jsonManufacturer.get(Requester.ParameterKey.NAME).getAsString();

                                                                        Manufacturer manufacturer = new Manufacturer();
                                                                        manufacturer.setId(manufacturerId);
                                                                        manufacturer.setProductId(product.getId());
                                                                        manufacturer.setName(manufacturerName);
                                                                        product.setManufacturer(manufacturer);
                                                                    }
                                                                }
                                                            }

                                                            products.add(product);
                                                        }
                                                    }

                                                }
                                            }

                                            WishList wishList = new WishList();
                                            wishList.setId(wishListId);
                                            wishList.setProducts(products);
                                            action.setWishList(wishList);
                                        }
                                    }
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
            action.setWishList(new WishList());
            actions.add(action);
        }
        return actions;
    }
}
