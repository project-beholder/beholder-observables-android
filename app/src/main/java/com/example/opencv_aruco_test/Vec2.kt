package com.example.opencv_aruco_test

import kotlin.math.atan2
import kotlin.math.sqrt

class Vec2 (var x: Double, var y: Double){
//    companion object Factory {
//        fun add(a: Vec2, b: Vec2):
//    }

    operator fun plus(b: Vec2) = Vec2(x + b.x, y + b.y)
    operator fun minus(b: Vec2) = Vec2(x - b.x, y - b.y)
    operator fun times(s: Double) = Vec2(x * s, y * s)

    fun clone() = Vec2(x, y)
    fun mag() = sqrt(x * x + y * y)
    fun mag2() = (x * x + y * y)
    fun normal() = Vec2(x / this.mag(), y / this.mag())
    fun getAngle() = atan2(y, x)
    fun angleBetween(b: Vec2) = atan2(
        x * b.y - y * b.x,
        x * b.x + y * b.y
    )
    fun dot(b: Vec2) = (x * b.x) + (y * b.y)
    fun dist(b: Vec2) = (this - b).mag()
    fun dist2(b: Vec2) = (this - b).mag2()
}