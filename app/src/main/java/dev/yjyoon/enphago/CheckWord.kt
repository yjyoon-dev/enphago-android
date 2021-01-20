package dev.yjyoon.enphago

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.*
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import java.lang.Exception

class CheckWord {
    companion object{
        const val OK = 0
        const val BLANK_INPUT = 1
        const val TOO_SHORT = 2
        const val ALREADY_USED = 3
        const val INVALID_WORD = 4
    }
    private val baseUrl = "https://stdict.korean.go.kr/api/search.do?certkey_no=2231&key="
    private val apiKey = "DD142E025E13B1072F2AF6E6C5D0A602"
    private val opt = "&type_search=search&advanced=y&pos=1,2,3&q="
    private var usedWordSet = mutableSetOf<String>()

    suspend fun check(word: String): Int{

        if(word.isEmpty()) return BLANK_INPUT

        if(word.length < 2) return TOO_SHORT

        if(usedWordSet.contains(word)) return ALREADY_USED

        val isValid = GlobalScope.async(Dispatchers.IO) {
            val url = baseUrl + apiKey + opt + word
            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(URL(url).openConnection().getInputStream())
            val root = xml.documentElement
            val nodeList = root.getElementsByTagName("total")
            val total = nodeList.item(0) as Element
            total.textContent.toString() != "0"
        }.await()

        if(!isValid) return INVALID_WORD

        usedWordSet.add(word)
        return OK
    }
}