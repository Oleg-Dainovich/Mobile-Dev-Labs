package com.example.calculator

public class MyNotification {
    public var id: String? = "";
    public lateinit var expression: String;
    public lateinit var result: String;

    constructor() {

    }

    constructor(_id: String?, _expression: String, _result: String) {
        id = _id;
        expression = _expression;
        result = _result;
    }
}