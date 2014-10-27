package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.cloud.models.WishListProductPictureModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListProductPictureRequest extends AbstractRequest<Multimedia> implements IRequest<WishListProductPictureModel, Multimedia> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/cdn/retrieve";

    @Override
    public void executeAsync(WishListProductPictureModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<WishListProductPictureModel> getDataClass() {
        return WishListProductPictureModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Multimedia parse(String response) {
        Multimedia multimedia = null;

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.MULTIMEDIA)) {
                JsonElement jsonElementMult = jsonObject.get(Requester.ParameterKey.MULTIMEDIA);
                if (!jsonElementMult.isJsonNull() && jsonElementMult.isJsonObject()) {
                    JsonObject jsonMultimedia = jsonElementMult.getAsJsonObject();

                    String url = null;
                    if (jsonMultimedia.has(Requester.ParameterKey.URL)) {
                        url = jsonMultimedia.get(Requester.ParameterKey.URL).getAsString();
                    }

                    multimedia = new Multimedia();
                    multimedia.setUrl(url);
                }
            }
        }

        return multimedia;
    }

    @Override
    protected Multimedia debugParse() {
        return null;
    }
}
