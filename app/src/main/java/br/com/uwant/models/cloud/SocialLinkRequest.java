package br.com.uwant.models.cloud;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.SocialLinkModel;
import br.com.uwant.models.cloud.models.SocialRegisterModel;
import br.com.uwant.models.databases.UserDatabase;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class SocialLinkRequest extends AbstractRequest<Boolean> implements IRequest<SocialLinkModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/social/link";

    public SocialLinkRequest() {
        super();
    }

    public SocialLinkRequest(Context context) {
        super(context);
    }

    @Override
    public void executeAsync(SocialLinkModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<SocialLinkModel> getDataClass() {
        return SocialLinkModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Boolean parse(String response) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.has(Requester.ParameterKey.LINKED)) {
                return jsonObject.get(Requester.ParameterKey.LINKED).getAsBoolean();
            }
        }

        return null;
    }

    @Override
    protected Boolean debugParse() {
        return true;
    }
}
