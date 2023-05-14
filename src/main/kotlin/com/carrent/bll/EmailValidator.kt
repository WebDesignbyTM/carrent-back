package com.carrent.bll

fun EmailValidator() = Regex("[a-zA-Z0-9\\-.]+@[a-zA-Z0-9\\-.]+\\.[a-zA-Z0-9\\-.]+")
//fun EmailValidator() = Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$")