package br.com.uwant.models.cloud;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.UserUpdateModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class UserUpdateRequest extends AbstractRequest<User> implements IRequest<UserUpdateModel, User> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/update";

    private String mMail;

    @Override
    public void executeAsync(UserUpdateModel data, OnRequestListener listener) {
        this.mMail = data.getMail();
        execute(data, listener);
    }

    @Override
    public Class<UserUpdateModel> getDataClass() {
        return UserUpdateModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected User parse(String response) {
        User user = User.getInstance();
        user.setMail(mMail);
        return user;
    }

    @Override
    protected User debugParse() {
        return parse(null);
    }
}
