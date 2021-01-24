package dev.yjyoon.enphago

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class SearchWord {
    private val baseUrl = "https://stdict.korean.go.kr/api/search.do?certkey_no=2231&key="
    private val apiKey = "DD142E025E13B1072F2AF6E6C5D0A602"
    private val opt = "&type_search=search&advanced=y&pos=1,2,3&q="

    suspend fun search(word:String): SearchResult? {
        return try{
            val result = withContext(Dispatchers.IO) {
                val url = baseUrl + apiKey + opt + word
                val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(URL(url).openConnection().getInputStream())
                val root = xml.documentElement
                val pos = root.getElementsByTagName("pos").item(0).textContent.toString()
                val def = root.getElementsByTagName("definition").item(0).textContent.toString()
                SearchResult(word, pos, def)
            }
            result
        } catch (e: Exception) {
            null
        }
    }
}