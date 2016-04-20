package com.example.lucasyang.myapplication;

import android.text.Html;
import android.text.Spanned;

import java.util.Objects;

/**
 * Created by lucasyang on 4/18/16.
 */
public class Issue implements Comparable<Issue> {

    String id;
    String title;
    String updated_at;
    String body;
    String comments;
    String comments_url;

    // constructor
    Issue(String id, String title, String updated_at, String body, String comments, String comments_url) {
        this.id = id;
        this.title = title;
        this.updated_at = updated_at;
        this.body = body;
        this.comments = comments;
        this.comments_url = comments_url;
    }

    @Override
    public String toString() {
        String s = "\nID: " + id + ";\nTITLE: " + title + ";\nUPDATED_AT: " + updated_at +
                ";\nBODY: " + body + ";\nCOMMENTS: " + comments + ";\nCOMMENTS_URL: " + comments_url +
                "\n\n*** *** *** E_N_D  O_F  I_S_S_U_E *** *** *** \n\n"; // May be partially blocked
        return s;
    }

    public String toAnchorString() {
        String s = "<br/>ID: " + id + ";<br/>TITLE: " + title + ";<br/>UPDATED_AT: " + updated_at +
                ";<br/>BODY: " + body + ";<br/>COMMENTS: " + comments + ";<br/>COMMENTS_URL: " + comments_url +
                "<br/><br/><br/><br/>";
        String s2 = "<a href='" + comments_url + "'>" + s + "</a>";
        return s2;
    }

    @Override
    public int compareTo(Issue i) {
        return this.updated_at.compareTo(i.updated_at);
    }

}
