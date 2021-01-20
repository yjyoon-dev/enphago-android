package dev.yjyoon.enphago

data class Chat(val writer: Int, val message: String){
    companion object{
        const val USER = 0
        const val ENPHAGO = 1
    }
}
