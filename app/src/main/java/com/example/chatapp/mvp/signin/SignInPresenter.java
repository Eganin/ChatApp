package com.example.chatapp.mvp.signin;

import android.util.Pair;

import com.example.chatapp.common.InfoUserSignIn;
import com.example.chatapp.contacts.ContactException;
import com.example.chatapp.exception.NoInfoFromEditTextException;

import static com.example.chatapp.contacts.ContactException.ToastText.TOAST_TEXT_AUTH_COMPLETE;
import static com.example.chatapp.contacts.ContactException.ToastText.TOAST_TEXT_AUTH_FAILED;
import static com.example.chatapp.mvp.signin.SignInModel.firebaseAuth;

public class SignInPresenter {

    public SignInView view;
    private final SignInModel model;

    public static String name;

    public SignInPresenter(SignInModel model) {
        this.model = model;
    }

    public void attachView(SignInView view) {
        this.view = view;
    }

    public void detachView() {
        view = null;
    }

    public void initDB() {
        model.initDB(new SignInModel.initCallBack() {
            @Override
            public void init() {

            }
        });

    }

    public void signUpUser(boolean loginModeActive) {
        Pair<String, String> pairEditTextResult;
        try {
            // проверяем что ввел пользователь
            if ((pairEditTextResult = checkingValuesEditTexts(loginModeActive)) != null) {
                loginSignUpUser(pairEditTextResult.first, pairEditTextResult.second, loginModeActive);
            }
        } catch (NoInfoFromEditTextException e) {
            handlerException(e);
        }
    }


    private void loginSignUpUser(String email, String password, boolean loginModeActive) {
        // метод отвечает за добавление пользователя в Firebase
        // проверяем регистрируется или логинится пользователь
        if (loginModeActive) {
            boolean isNewUser = false;
            // sign in user
            model.loginOrSignUpUser(new SignInModel.loginComplete() {
                @Override
                public void startView() {
                    setToast(TOAST_TEXT_AUTH_COMPLETE);
                    startIntent();
                }
            }, new SignInModel.loginFailed() {
                @Override
                public void setToast() {
                    SignInPresenter.this.setToast(TOAST_TEXT_AUTH_FAILED);
                }
            }, firebaseAuth.signInWithEmailAndPassword(email, password), isNewUser, view);
        } else {
            boolean isNewUser = true;
            // create authorization user
            model.loginOrSignUpUser(new SignInModel.loginComplete() {
                @Override
                public void startView() {
                    setToast(TOAST_TEXT_AUTH_COMPLETE);
                    startIntent();
                }
            }, new SignInModel.loginFailed() {
                @Override
                public void setToast() {
                    SignInPresenter.this.setToast(TOAST_TEXT_AUTH_FAILED);
                }
            }, firebaseAuth.createUserWithEmailAndPassword(email, password), isNewUser, view);
        }
    }

    private Pair<String, String> checkingValuesEditTexts(boolean loginModeActive)
            throws NoInfoFromEditTextException {

        InfoUserSignIn infoUserSignIn = view.getInfoFromUser();

        String email = infoUserSignIn.getEmail();
        String password = infoUserSignIn.getPassword();
        String repeatPassword = infoUserSignIn.getRepeatPassword();
        String nickName = infoUserSignIn.getNickName();
        name = nickName;


        if (loginModeActive) {
            if (email.equals("")) {
                throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_EMAIL);
            }
            if (password.equals("")) {
                throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_PASSWORD);
            }
            return new Pair<String, String>(email, password);
        }
        if (!email.equals("") && !password.equals("") && repeatPassword.equals(password)) {
            return new Pair<String, String>(email, password);
        }
        if (nickName.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_NICKNAME);
        }
        if (email.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_EMAIL);
        }
        if (password.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_PASSWORD);
        }
        if (password.length() <= 6) {
            throw new NoInfoFromEditTextException(ContactException.Exception.LENGTH_PASSWORD);
        }
        if (repeatPassword.equals("")) {
            throw new NoInfoFromEditTextException(ContactException.Exception.NOT_FOUND_REPEAT_PASSWORD);
        }
        if (!repeatPassword.equals(password)) {
            throw new NoInfoFromEditTextException(ContactException.Exception.PASSWORD_DONT_MATCH);
        } else {
            throw new NoInfoFromEditTextException(ContactException.Exception.UNKNOWN);
        }
    }

    private void handlerException(NoInfoFromEditTextException exception) {
        // обрабатываем свою ошибку NoInfoFromEditTextException

        view.showToastException(exception);
    }

    public void setToast(String text) {
        view.showToast(text);
    }

    public void startIntent() {
        view.startIntentUserListView();
    }

    public void checkCurrentUser() {
        model.checkCurrentUser(new SignInModel.initCallBack() {
            @Override
            public void init() {
                startIntent();
            }
        });
    }
}
