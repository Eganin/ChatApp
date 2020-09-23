package com.example.chatapp;

public final class ContactException {

    static final class Exception{
        public static final String PASSWORD_DONT_MATCH = "Введенные пароли не совпадают";
        public static final String NOT_FOUND_PASSWORD = "Введите пароль";
        public static final String NOT_FOUND_REPEAT_PASSWORD = "Введите повторный пароль";
        public static final String NOT_FOUND_NICKNAME = "Введите никнейм";
        public static final String NOT_FOUND_EMAIL = "Введите email";
        public static final String LENGTH_PASSWORD = "Пароль должен содержать более 6 цифр";
        public static final String UNKNOWN = "Неизвестная ошибка";
    }

    static final class IntentKeys{
        public static final String NICKNAME = "NickName";
    }

}
