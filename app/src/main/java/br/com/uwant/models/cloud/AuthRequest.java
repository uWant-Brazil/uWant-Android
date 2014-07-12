package br.com.uwant.models.cloud;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.AuthModel;

public class AuthRequest extends AbstractRequest<User> implements IRequest<AuthModel, User> {

    private static final String ROUTE = "/mobile/authorize";
    private AuthModel mModel;

    @Override
    public void executeAsync(AuthModel data, OnRequestListener listener) {
        this.mModel = data;
        execute(data, listener);
    }

    @Override
    public Class<AuthModel> getDataClass() {
        return AuthModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected User parse(String response) {
        User user = User.getInstance();
        user.setLogin(mModel.getLogin());
        return user;
    }
}
