package br.com.uwant.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.Session;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.ConfigurationsActivity;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;

public abstract class UserUtil {

    public static final int RQ_FACEBOOK_LINK = 823;

    private static final String CONST_SWITCH_FACEBOOK_DIALOG = "SwitchFacebookDialog";
    private static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("publish_actions");
    private static final DialogInterface.OnClickListener DEFAULT_NEGATIVE_LISTENER = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Do nothing...
        }

    };

    public static boolean hasFacebook() {
        User user = User.getInstance();
        String token = user.getFacebookToken();
        String id = user.getFacebookId();
        return (token != null && !token.isEmpty() && id != null && !id.isEmpty());
    }

    public static void showFacebookDialog(final FragmentActivity activity) {
        showFacebookDialog(activity, DEFAULT_NEGATIVE_LISTENER);
    }

    public static void showFacebookDialog(final FragmentActivity activity, DialogInterface.OnClickListener negative) {
        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent it = new Intent(activity, ConfigurationsActivity.class);
                activity.startActivityForResult(it, RQ_FACEBOOK_LINK);
            }

        };

        AlertFragmentDialog afd = AlertFragmentDialog.create(
                activity.getString(R.string.text_attention),
                activity.getString(R.string.text_facebook_link),
                activity.getString(R.string.text_yes),
                positive,
                activity.getString(R.string.text_no),
                negative);

        afd.show(activity.getSupportFragmentManager(), CONST_SWITCH_FACEBOOK_DIALOG);
    }

    public static void shareFacebook(FragmentActivity activity, Session.StatusCallback callback, ProgressFragmentDialog progressDialog, WishList wishList, Multimedia multimedia) {
        Session session = Session.getActiveSession();
        if (session != null) {
            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(FACEBOOK_PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(activity, FACEBOOK_PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

//                WishListUtil.shareAction(activity, wishList, multimedia);
                WishListUtil.share(activity, wishList, multimedia);
            }
        } else {
            User user = User.getInstance();
            AccessToken accessToken = AccessToken.createFromExistingAccessToken(user.getFacebookToken(), null, null, null, FACEBOOK_PERMISSIONS);
            Session.openActiveSessionWithAccessToken(activity, accessToken, callback);
        }
    }

    private static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }


}
