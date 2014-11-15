package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.classes.Comment;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de obtenção das listas de desejos do usuário.
 */
public class WantModel extends AbstractJSONRequestModel {

    private Action action;
    private Comment comment;

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (this.action != null) {
            json.addProperty(Requester.ParameterKey.ACTION_ID, this.action.getId());
        } else if (this.comment != null) {
            json.addProperty(Requester.ParameterKey.COMMENT_ID, this.comment.getId());
        }
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.ACTION_WANT;
    }

}
