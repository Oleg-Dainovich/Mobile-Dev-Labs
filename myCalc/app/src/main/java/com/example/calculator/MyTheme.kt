package com.example.calculator

class MyTheme {
    var id: String? = "";
    lateinit var theme_id: String;
    lateinit var text_color: String;
    lateinit var bg_color: String;
    lateinit var num_bg_color: String;
    lateinit var button_bg_color: String;

    constructor() {

    }

    constructor(_id: String?, _theme_id: String, _text_color: String,
                _bg_color: String, _num_bg_color: String, _button_bg_color: String) {
        id = _id;
        theme_id = _theme_id;
        text_color = _text_color;
        bg_color = _bg_color;
        num_bg_color = _num_bg_color;
        button_bg_color = _button_bg_color;
    }
}