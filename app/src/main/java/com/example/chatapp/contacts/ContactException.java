package com.example.chatapp.contacts;

public final class ContactException {

    public static final class Exception{
        public static final String PASSWORD_DONT_MATCH = "Введенные пароли не совпадают";
        public static final String NOT_FOUND_PASSWORD = "Введите пароль";
        public static final String NOT_FOUND_REPEAT_PASSWORD = "Введите повторный пароль";
        public static final String NOT_FOUND_NICKNAME = "Введите никнейм";
        public static final String NOT_FOUND_EMAIL = "Введите email";
        public static final String LENGTH_PASSWORD = "Пароль должен содержать более 6 цифр";
        public static final String UNKNOWN = "Неизвестная ошибка";
    }

    public static final class IntentKeys{
        public static final String NICKNAME = "NickName";
        public static final String RECIPIENT_USER_ID = "recipientUserId";
        public static final String RECIPIENT_USER_NAME = "recipientUserName";
    }

    public static final class TAG{
        public static final String TAG = "SignInActivity";
    }
    public static final class Text{
        public static final String CLEAR_EDIT_TEXT = "";
        public static final String TEXT_INTENT_IMAGE = "Выберите изображение";
    }

    public static final class Types{
        public static final int MAX_LENGTH_MESSAGE = 500;
        public static final String MIME_TYPE_IMAGES = "image/*";
        public static final Integer RC_IMAGE_PICKER = 123;
    }

    public static final class ToggleLoginModeTexts{
        public static final String SIGN_UP = "Sign Up";
        public static final String SWITCH_LOG_IN = "Tap to Log In";
        public static final String LOG_IN = "Log In";
        public static final String SWITCH_SIGN_UP = "Tap to Sign Up";
    }

    public static final class NameFavoriteUser{
        public static final String FAVORITE="Избранное";
    }

}
