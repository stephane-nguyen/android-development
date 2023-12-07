package com.tps.stephanenguyentp5

var toto = ""
var tata : String = ""
var n : Int = 0
var nDouble : Double = 0.0
var nFloat : Float = 0f

open class myClass {} // use open to extend to subclasses
interface myInterface {}
enum class myEnum {}
class subClass : myClass() {} // extends

fun myFun() {}
fun myStringFun(): String = ""
fun addition(a: Int, b: Int): Int {
    return a + b
}

// Overload on Java
@JvmOverloads
fun substraction(a: Int, b:Int = 5): Int = a - b

var resSub = substraction(5,9)
var resSubWithDefaultParam = substraction(5)
var resAdd = addition(2,5)

// Call function inside of function
fun superFun(call: () -> Unit) {}// unit == void
fun superFun1(call: (Int, Int) -> Int) {}

fun test = superFun1(::addition)

// hello is a method, extension of the class String
//fun String.hello(name: String) = "Hello $name"
fun String.hello() = "Hello $this"

var hello = "Mick".hello() // Hello Mick
