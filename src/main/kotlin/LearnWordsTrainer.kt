import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int,
) {
    override fun toString(): String {
        return "$original, $translate, $correctAnswersCount"
    }
}

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index: Int, word: Word -> " ${index + 1} – ${word.translate}" }
        .joinToString(
            separator = "\n",
            prefix = "\n${this.correctAnswer.original}\n",
            postfix = "\n ----------\n 0 - Меню",
        )
    return variants
}

data class Statistic(
    val totalWords: Int,
    val learnedCount: Int,
    val percentLearned: Int
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val fileName: String = "words.txt",
    private val learnedAnswerCount: Int = 3,
    private val countOfQuestionWords: Int = 4
) {
    var question: Question? = null
        private set
    private val dictionary = loadDictionary()

    fun getStatistic(): Statistic {
        val totalWords = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.size
        val percentLearned = (learnedCount * 100) / totalWords
        return Statistic(totalWords, learnedCount, percentLearned)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < learnedAnswerCount }
        if (notLearnedList.isEmpty()) return null

        val questionWords = if (notLearnedList.size < countOfQuestionWords) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.shuffled()
            notLearnedList.shuffled().take(countOfQuestionWords) +
                    learnedList.take(countOfQuestionWords - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(countOfQuestionWords)
        }.shuffled()

        val correctAnswer = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerId: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerId) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        try {
            val wordsFile = File(fileName)
            if (!wordsFile.exists()) {
                File("words.txt").copyTo(wordsFile)
            }

            val dictionary = mutableListOf<Word>()
            wordsFile.createNewFile()
            val lines: List<String> = wordsFile.readLines()

            for (line in lines) {
                val lineParts = line.split("|")
                val word = Word(
                    original = lineParts[0],
                    translate = lineParts[1],
                    correctAnswersCount = lineParts.getOrNull(2)?.toInt() ?: 0
                )
                dictionary.add(word)
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл словаря. ")
        }

    }

    private fun saveDictionary() {
        val file = File(fileName)
        val updateLines =
            dictionary.joinToString("\n") { it.original + "|" + it.translate + "|" + it.correctAnswersCount }
        file.writeText(updateLines)
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}
