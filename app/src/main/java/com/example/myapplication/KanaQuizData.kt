package com.example.myapplication

import java.io.Serializable

data class KanaQuestion(
    val prompt: String,
    val answer: String,
    val translation: String,
    val options: List<String> = emptyList()
) : Serializable

object KanaQuizData {
    // 5 Hiragana words for Identification
    val identificationHiragana = listOf(
        KanaQuestion("ねこ", "neko", "Cat"),
        KanaQuestion("いぬ", "inu", "Dog"),
        KanaQuestion("さくら", "sakura", "Cherry Blossom"),
        KanaQuestion("みず", "mizu", "Water"),
        KanaQuestion("おはよ", "ohayo", "Good Morning")
    )

    // 5 Katakana words for Identification
    val identificationKatakana = listOf(
        KanaQuestion("カメラ", "kamera", "Camera"),
        KanaQuestion("テレビ", "terebi", "Television"),
        KanaQuestion("パン", "pan", "Bread"),
        KanaQuestion("トイレ", "toire", "Toilet"),
        KanaQuestion("アメリカ", "amerika", "America")
    )

    // Hiragana Characters for Multiple Choice
    val hiraganaCharacters = listOf(
        KanaQuestion("あ", "a", "a"), KanaQuestion("い", "i", "i"), KanaQuestion("う", "u", "u"),
        KanaQuestion("え", "e", "e"), KanaQuestion("お", "o", "o"), KanaQuestion("か", "ka", "ka"),
        KanaQuestion("き", "ki", "ki"), KanaQuestion("く", "ku", "ku"), KanaQuestion("け", "ke", "ke"),
        KanaQuestion("こ", "ko", "ko"), KanaQuestion("さ", "sa", "sa"), KanaQuestion("し", "shi", "shi"),
        KanaQuestion("す", "su", "su"), KanaQuestion("せ", "se", "se"), KanaQuestion("そ", "so", "so")
    )

    // Katakana Characters for Multiple Choice
    val katakanaCharacters = listOf(
        KanaQuestion("ア", "a", "a"), KanaQuestion("イ", "i", "i"), KanaQuestion("ウ", "u", "u"),
        KanaQuestion("エ", "e", "e"), KanaQuestion("オ", "o", "o"), KanaQuestion("カ", "ka", "ka"),
        KanaQuestion("キ", "ki", "ki"), KanaQuestion("ク", "ku", "ku"), KanaQuestion("ケ", "ke", "ke"),
        KanaQuestion("コ", "ko", "ko"), KanaQuestion("サ", "sa", "sa"), KanaQuestion("シ", "shi", "shi"),
        KanaQuestion("ス", "su", "su"), KanaQuestion("セ", "se", "se"), KanaQuestion("ソ", "so", "so")
    )
}
