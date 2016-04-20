package com.example.lucasyang.myapplication;

/**
 * Created by lucasyang on 4/20/16.
 */
public class Comment {
    String body;
    String user_login;
    String user_id;

    //constructor
    public Comment(String user_login, String user_id, String body) {
        this.body = body;
        this.user_id = user_id;
        this.user_login = user_login;
    }

    @Override
    public String toString() {
        String s = "\nUSER_ID: " + user_id + ";\nUSER_LOGIN: " + user_login +
                ";\nBODY: " + body + "\n\n\n ### E_N_D  O_F  T_H_I_S  C_O_M_M_E_N_T ### \n\n\n";  // May be partially blocked

        return s;
    }
}
