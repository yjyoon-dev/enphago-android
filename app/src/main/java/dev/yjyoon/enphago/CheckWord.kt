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
        const val MISMATCHED_WORD = 3
        const val ALREADY_USED = 4
        const val INVALID_WORD = 5
        const val INTERNET_DISCONNECTED = 6
    }

    var usedWordSet = mutableSetOf<String>()

    val convertMap =
            mapOf("라" to "나","락" to "낙","란" to "난","랄" to "날",
                    "람" to "남","랍" to "납","랏" to "낫","랑" to "낭",
                    "략" to "약","냑" to "약","량" to "양","냥" to "양",
                    "렁" to "넝","려" to "여","녀" to "여","력" to "역",
                    "녁" to "역","련" to "연","년" to "연","렬" to "열",
                    "녈" to "열","렴" to "염","념" to "염","렵" to "엽",
                    "령" to "영","녕" to "영","로" to "노","록" to "녹",
                    "론" to "논","롤" to "놀","롬" to "놈","롭" to "놉",
                    "롯" to "놋","롱" to "농","뢰" to "뇌","료" to "요",
                    "뇨" to "요","룡" to "용","뇽" to "용","루" to "누",
                    "룩" to "눅","룬" to "눈","룰" to "눌","룸" to "눔",
                    "룻" to "눗","룽" to "눙","류" to "유","뉴" to "유",
                    "륙" to "육","뉵" to "육","륜" to "윤","률" to "율",
                    "륭" to "융","르" to "느","륵" to "늑","른" to "는",
                    "를" to "늘","름" to "늠","릅" to "늡","릇" to "늣",
                    "릉" to "능","래" to "내","랙" to "낵","랜" to "낸",
                    "랠" to "낼","램" to "냄","랩" to "냅","랫" to "냇",
                    "랭" to "냉","례" to "예","녜" to "예","뢰" to "뇌",
                    "리" to "이","니" to "이","린" to "인","닌" to "인",
                    "릴" to "일","닐" to "일","림" to "임","님" to "임",
                    "립" to "입","닙" to "입","릿" to "잇","닛" to "잇",
                    "링" to "잉","닝" to "잉")

    private val baseUrl = "https://stdict.korean.go.kr/api/search.do?certkey_no=2231&key="
    private val apiKey = "DD142E025E13B1072F2AF6E6C5D0A602"
    private val opt = "&type_search=search&advanced=y&pos=1,2,3&q="

    suspend fun check(word: String, enphagoWord: String): Int {

        if(word.isEmpty()) return BLANK_INPUT

        if(word.length < 2) return TOO_SHORT

        if(!isMatched(enphagoWord, word)) return MISMATCHED_WORD

        if(usedWordSet.contains(word)) return ALREADY_USED

        try {
            val isValid: Boolean = GlobalScope.async(Dispatchers.IO) {
                val url = baseUrl + apiKey + opt + word
                val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(URL(url).openConnection().getInputStream())
                val root = xml.documentElement
                val total = root.getElementsByTagName("total").item(0).textContent.toString()
                total != "0"
            }.await()

            if(!isValid) return INVALID_WORD

        } catch (e: Exception) {
            return INTERNET_DISCONNECTED
        }

        usedWordSet.add(word)
        return OK
    }
    fun isMatched(prevWord: String, curWord:String): Boolean{
        if(prevWord == "init") return true

        var tail = prevWord.substring(prevWord.length-1)
        val head = curWord.substring(0..0)

        if(tail == head) return true;

        if(convertMap.containsKey(tail)) tail = convertMap.get(tail)!!

        return tail == head
    }
}