package dev.yjyoon.enphago

data class Chat(val writer: Int, val message: String){
    companion object{
        val USER = 0
        val ENPHAGO = 1
    }
}
