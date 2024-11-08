package com.nistech.coin_clicker2.utils

fun String.removeScheme(): String {
    return this.substring(this.lastIndexOf(':') + 3)
}