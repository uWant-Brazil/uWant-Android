package br.com.uwant.models.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.AuthModel;
import br.com.uwant.models.cloud.models.ManufacturersModel;
import br.com.uwant.utils.DateUtil;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ManufacturersRequest extends AbstractRequest<List<Manufacturer>> implements IRequest<ManufacturersModel, List<Manufacturer>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/manufacturer/list";

    @Override
    public void executeAsync(ManufacturersModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ManufacturersModel> getDataClass() {
        return ManufacturersModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<Manufacturer> parse(String response) {
        List<Manufacturer> manufacturers = new ArrayList<Manufacturer>();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.MANUFACTURERS)) {
                JsonElement jsonManufacturers = jsonObject.get(Requester.ParameterKey.MANUFACTURERS);
                if (jsonManufacturers.isJsonArray()) {
                    JsonArray arrayManufacturers = jsonManufacturers.getAsJsonArray();
                    for (int i = 0; i < arrayManufacturers.size(); i++) {
                        JsonElement jsonArray = arrayManufacturers.get(i);
                        if (jsonArray.isJsonObject()) {
                            JsonObject object = jsonArray.getAsJsonObject();
                            if (object.has(Requester.ParameterKey.ID) && object.has(Requester.ParameterKey.NAME)) {
                                long id = object.get(Requester.ParameterKey.ID).getAsLong();
                                String name = object.get(Requester.ParameterKey.NAME).getAsString();
                                Manufacturer manufacturer = new Manufacturer();
                                manufacturer.setId(id);
                                manufacturer.setName(name);
                                manufacturers.add(manufacturer);
                            }
                        }
                    }
                }
            }
        }
        return manufacturers;
    }

    @Override
    protected List<Manufacturer> debugParse() {
        List<Manufacturer> manufacturers = new ArrayList<Manufacturer>();
        return manufacturers;
    }
}
