package com.mltrading;

/**
 * Created by gmo on 01/02/2017.
 */
import com.mltrading.ml.CacheMLStock;

import static spark.Spark.*;

public class server {

    public static void main(String[] args) {
        get("/save", (req, res) -> save());
        get("/load", (req, res) -> load());
        get("/delete", (req, res) -> delete());
        get("/ping", (req, res) -> ping());
    }

    public static String save() {
        CacheMLStock.saveDB();
        return "ok";
    }

    public static String load() {
        CacheMLStock.loadDB();
        return "ok";
    }

    public static String delete() {
        CacheMLStock.deleteDB();
        return "ok";
    }


    public static String ping() {

        return "ok";
    }
}