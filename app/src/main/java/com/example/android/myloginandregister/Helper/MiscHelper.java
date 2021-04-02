package com.example.android.myloginandregister.Helper;

import java.util.regex.Pattern;

public class MiscHelper {

    public boolean isEmailValid(String email_string)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email_string == null)
            return false;
        return pat.matcher(email_string).matches();
    }

    }
