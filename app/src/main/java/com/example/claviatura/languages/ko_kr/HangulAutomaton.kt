package com.example.claviatura.languages.ko_kr

class HangulAutomaton {

    private val choList = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
        'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val jungList = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
        'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    )

    private val jongList = charArrayOf(
        '\u0000', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
        'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private var cho = -1
    private var jung = -1
    private var jong = 0

    fun reset() {
        cho = -1
        jung = -1
        jong = 0
    }

    fun isCho(c: Char): Boolean = choList.contains(c)
    fun isJung(c: Char): Boolean = jungList.contains(c)

    fun append(c: Char): String {
        return if (isCho(c)) {
            handleChoInput(c)
        } else if (isJung(c)) {
            handleJungInput(c)
        } else {
            val committed = getComposedChar()
            reset()
            committed + c
        }
    }

    private fun handleChoInput(c: Char): String {
        val newCho = choList.indexOf(c)

        if (cho == -1) {
            cho = newCho
            return ""
        } else if (jung == -1) {
            val committed = choList[cho].toString()
            cho = newCho
            return committed
        } else if (jong == 0) {
            val newJong = jongList.indexOf(c)
            if (newJong > 0) {
                jong = newJong
                return ""
            } else {
                val committed = getComposedChar()
                reset()
                cho = newCho
                return committed
            }
        } else {
            val combinedJong = combineJong(jong, newCho)
            if (combinedJong > 0) {
                jong = combinedJong
                return ""
            } else {
                val committed = getComposedChar()
                reset()
                cho = newCho
                return committed
            }
        }
    }

    private fun handleJungInput(c: Char): String {
        val newJung = jungList.indexOf(c)

        if (cho == -1) {
            val committed = if (jung != -1) jungList[jung].toString() else ""
            reset()
            return committed + c
        } else if (jung == -1) {
            // 초성만 있을 때 모음('ㅣ' 포함) 입력 시 초성+중성 음절 생성
            jung = newJung
            return ""
        } else if (jong == 0) {
            // 초성+중성 상태에서 모음 입력 시 이중모음 결합 시도
            val combinedJung = combineJung(jung, newJung)
            if (combinedJung != -1) {
                jung = combinedJung
                return ""
            } else {
                val committed = getComposedChar()
                reset()
                jung = newJung
                return committed
            }
        } else {
            // 종성이 존재하는 상태에서 모음 입력 시 종성 분리
            val (prevJong, nextCho) = splitJong(jong)
            jong = prevJong
            val committed = getComposedChar()

            reset()
            cho = nextCho
            jung = newJung
            return committed
        }
    }

    private fun combineJung(j1: Int, j2: Int): Int {
        val char1 = jungList[j1]
        val char2 = jungList[j2]

        if (char2 == 'ㅣ') {
            val combined = when (char1) {
                'ㅏ' -> 'ㅐ'
                'ㅓ' -> 'ㅔ'
                'ㅑ' -> 'ㅒ'
                'ㅕ' -> 'ㅖ'
                'ㅗ' -> 'ㅚ'
                'ㅜ' -> 'ㅟ'
                'ㅡ' -> 'ㅢ'
                'ㅘ' -> 'ㅙ'
                'ㅝ' -> 'ㅞ'
                else -> null
            }
            if (combined != null) return jungList.indexOf(combined)
        }

        if (char1 == 'ㅗ') {
            when (char2) {
                'ㅏ' -> return jungList.indexOf('ㅘ')
                'ㅐ' -> return jungList.indexOf('ㅙ')
            }
        } else if (char1 == 'ㅜ') {
            when (char2) {
                'ㅓ' -> return jungList.indexOf('ㅝ')
                'ㅔ' -> return jungList.indexOf('ㅞ')
            }
        }
        return -1
    }

    private fun combineJong(j1: Int, c2Idx: Int): Int {
        val c2 = choList[c2Idx]
        val j1Char = jongList[j1]

        val combined = when {
            j1Char == 'ㄱ' && c2 == 'ㅅ' -> 'ㄳ'
            j1Char == 'ㄴ' && c2 == 'ㅈ' -> 'ㄵ'
            j1Char == 'ㄴ' && c2 == 'ㅎ' -> 'ㄶ'
            j1Char == 'ㄹ' && c2 == 'ㄱ' -> 'ㄺ'
            j1Char == 'ㄹ' && c2 == 'ㅁ' -> 'ㄻ'
            j1Char == 'ㄹ' && c2 == 'ㅂ' -> 'ㄼ'
            j1Char == 'ㄹ' && c2 == 'ㅅ' -> 'ㄽ'
            j1Char == 'ㄹ' && c2 == 'ㅌ' -> 'ㄾ'
            j1Char == 'ㄹ' && c2 == 'ㅍ' -> 'ㄿ'
            j1Char == 'ㄹ' && c2 == 'ㅎ' -> 'ㅀ'
            j1Char == 'ㅂ' && c2 == 'ㅅ' -> 'ㅄ'
            else -> null
        }
        return if (combined != null) jongList.indexOf(combined) else 0
    }

    private fun splitJong(jongIdx: Int): Pair<Int, Int> {
        val jChar = jongList[jongIdx]
        return when (jChar) {
            'ㄳ' -> Pair(jongList.indexOf('ㄱ'), choList.indexOf('ㅅ'))
            'ㄵ' -> Pair(jongList.indexOf('ㄴ'), choList.indexOf('ㅈ'))
            'ㄶ' -> Pair(jongList.indexOf('ㄴ'), choList.indexOf('ㅎ'))
            'ㄺ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㄱ'))
            'ㄻ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㅁ'))
            'ㄼ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㅂ'))
            'ㄽ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㅅ'))
            'ㄾ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㅌ'))
            'ㄿ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㅍ'))
            'ㅀ' -> Pair(jongList.indexOf('ㄹ'), choList.indexOf('ㅎ'))
            'ㅄ' -> Pair(jongList.indexOf('ㅂ'), choList.indexOf('ㅅ'))
            else -> Pair(0, choList.indexOf(jChar))
        }
    }

    fun delete(): Boolean {
        if (jong > 0) {
            val jChar = jongList[jong]
            jong = when (jChar) {
                'ㄳ' -> jongList.indexOf('ㄱ')
                'ㄵ', 'ㄶ' -> jongList.indexOf('ㄴ')
                'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ' -> jongList.indexOf('ㄹ')
                'ㅄ' -> jongList.indexOf('ㅂ')
                else -> 0
            }
            return true
        } else if (jung != -1) {
            val jChar = jungList[jung]
            jung = when (jChar) {
                'ㅐ' -> jungList.indexOf('ㅏ')
                'ㅔ' -> jungList.indexOf('ㅓ')
                'ㅒ' -> jungList.indexOf('ㅑ')
                'ㅖ' -> jungList.indexOf('ㅕ')
                'ㅘ', 'ㅙ', 'ㅚ' -> jungList.indexOf('ㅗ')
                'ㅝ', 'ㅞ', 'ㅟ' -> jungList.indexOf('ㅜ')
                'ㅢ' -> jungList.indexOf('ㅡ')
                else -> -1
            }
            return true
        } else if (cho != -1) {
            cho = -1
            return true
        }
        return false
    }

    fun getComposedChar(): String {
        if (cho == -1) return ""
        if (jung == -1) return choList[cho].toString()

        val unicode = 0xAC00 + (cho * 21 * 28) + (jung * 28) + jong
        return unicode.toChar().toString()
    }
}