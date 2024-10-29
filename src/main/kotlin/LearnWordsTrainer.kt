import java.io.File

data class Statistic(
    val totalWords: Int,
    val learnedCount: Int,
    val percentLearned: Int
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(private val learnedAnswerCount: Int = 3, private val countOfQuestionWords: Int = 4) {
    private var question: Question? = null
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
            if (question == null) return false
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerId) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        try {
            val dictionary = mutableListOf<Word>()
            val wordsFile = File("words.txt")
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
        }catch (e: IndexOutOfBoundsException){
            throw IllegalStateException("Некорректный файл словаря. ")
        }

    }

    private fun saveDictionary(dictionary: List<Word>) {
        val file = File("words.txt")
        val updateLines =
            dictionary.joinToString("\n") { it.original + "|" + it.translate + "|" + it.correctAnswersCount }
        file.writeText(updateLines)
    }
}

