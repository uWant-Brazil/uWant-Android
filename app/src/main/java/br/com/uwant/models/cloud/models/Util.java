package br.com.uwant.models.cloud.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.Requester;

/**
 * Created by Cleibson Gomes da Silva on 21/10/2014.
 */
public class Util {

   public static JsonArray listProductsToJson(WishListUpdateModel.Type type, List<Product> products){
       JsonArray arrayProducts = new JsonArray();

       if (products != null) {
           for (Product product : products) {
               JsonObject jsonProduct = new JsonObject();

               switch (type){
                   case DELETE:
                       jsonProduct.addProperty(Requester.ParameterKey.ID, product.getId());
                       break;
                   case INSERT:
                       Manufacturer manufacturer = product.getManufacturer();
                       JsonObject jsonManufacturer = new JsonObject();
                       jsonManufacturer.addProperty(Requester.ParameterKey.NAME, manufacturer.getName());

                       jsonProduct.addProperty(Requester.ParameterKey.NAME, product.getName());
                       jsonProduct.addProperty(Requester.ParameterKey.NICK_NAME, product.getName());
                       jsonProduct.add(Requester.ParameterKey.MANUFACTURER, jsonManufacturer);
               }
               arrayProducts.add(jsonProduct);
           }

       }

       return arrayProducts;
   }
}
