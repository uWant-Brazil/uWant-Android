package br.com.uwant.models.classes;

import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import br.com.uwant.models.cloud.Requester;

/**
 * Classe-pai de modelagem para todos os usuários do sistema. Aqui guardamos todas as informações
 * básicas de um usuário cadastrado em nosso sistema.
 */
public class Person implements Serializable {

    /**
     * Constante para análise durante a serialização dessa classe.
     */
    public static final String EXTRA = "extra_person";

    /**
     * Sexo
     */
    public enum Gender {
        FEMALE, MALE, UNKNOWN;
    }

    /**
     * Nível de amizade entre o usuário e o usuário logado ("você").
     */
    public enum FriendshipLevel {
        MUTUAL, WAITING_ME, WAITING_YOU, NONE;
    }

    /**
     * Identificador único.
     */
    private long id;

    /**
     * Flag para identificar se o usuário é amigo do usuário logado.
     */
    private boolean friend;

    /**
     * Nome do usuário.
     */
    private String name;

    /**
     * Login do usuário.
     */
    private String login;

    /**
     * E-mail do usuário.
     */
    private String mail;

    /**
     * Identificador único fornecido pelo Facebook -> Utilizado para vincular a rede social.
     */
    private String facebookId;

    /**
     * Data de nascimento do usuário.
     */
    private Date birthday;

    /**
     * Sexo do usuário.
     */
    private Gender gender;

    /**
     * Foto do usuário.
     */
    private Multimedia picture;

    /**
     * Nível de amizade do usuário com o usuário logado.
     */
    private FriendshipLevel friendshipLevel;

    public Person() {
    }

    public Person(long id, String login, String name) {
        this.id = id;
        this.login = login;
        this.name = name;
    }

    /**
     * Construtor-wrapper entre nossa entidade de usuário e a entidade de usuário do Facebook.
     * @param friend - Usuário do Facebook
     */
    public Person(GraphUser friend) {
        final String id = (String) friend.getProperty(Requester.ParameterKey.ID);
        final String mail = (String) friend.getProperty(Requester.ParameterKey.EMAIL);
        final String name = String.format("%s %s",
                friend.getProperty(Requester.ParameterKey.FIRST_NAME),
                friend.getProperty(Requester.ParameterKey.LAST_NAME));

        JSONObject go = (JSONObject) friend.getProperty(Requester.ParameterKey.PICTURE);
        if (go != null) {
            JSONObject picture = null;
            try {
                picture = go.getJSONObject(Requester.ParameterKey.DATA);
                if (picture != null && picture.has(Requester.ParameterKey.URL)) {
                    Multimedia pictureM = new Multimedia();
                    String url = picture.getString(Requester.ParameterKey.URL);
                    pictureM.setUrl(url);
                    this.picture = pictureM;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.name = name;
        this.mail = mail;
        this.facebookId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof Person) {
            Person person = (Person) o;

            if (!mail.equals(person.mail)) return false;
            if (mail.equals(person.mail)
                    && !name.equals(person.name)) return true;
            if (mail.equals(person.mail)
                    && (person.mail.equals(person.name) || mail.equals(name))) return true;

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 31 * name.hashCode();
        result = 31 * result + mail.hashCode();
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Multimedia getPicture() {
        return picture;
    }

    public void setPicture(Multimedia picture) {
        this.picture = picture;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isFriend() {
        return friendshipLevel == FriendshipLevel.MUTUAL;
    }

    public FriendshipLevel getFriendshipLevel() {
        return friendshipLevel;
    }

    public void setFriendshipLevel(FriendshipLevel friendshipLevel) {
        this.friendshipLevel = friendshipLevel;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
