package dev.yjyoon.enphago

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import java.lang.Exception

class CheckWord {
    val baseUrl = "https://stdict.korean.go.kr/api/search.do?certkey_no=2231&key="
    val apiKey = "DD142E025E13B1072F2AF6E6C5D0A602"
    val opt = "&type_search=search&advanced=y&pos=1&q="

    suspend fun isValid(word: String): Boolean{
        try{
            val result = GlobalScope.async(Dispatchers.IO) {
                val url = baseUrl + apiKey + opt + word
                val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(URL(url).openConnection().getInputStream())
                val root = xml.documentElement
                val nodeList = root.getElementsByTagName("total")
                val total = nodeList.item(0) as Element
                total.textContent
            }.await()
            return result.toString() != "0"
        } catch (e: Exception){
            e.printStackTrace()
            return false
        }

    }
}