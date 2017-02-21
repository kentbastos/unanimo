import groovy.json.JsonSlurper
import groovy.transform.ToString
import java.text.Normalizer

def input = new File ('.', 'input.json')
def jsonSlurper = new JsonSlurper()
def inputData = jsonSlurper.parseText(input.text)

//println inputData

def players = []
def wordsPerDay = [:]

inputData.days.each { day ->
	println "on traite le jour $day.index"

	Set<Word> allWordsOfADay = []

	day.players.each { player ->
		def currentPlayer = players.find { it.name == player.name }
		if(!currentPlayer) {
			currentPlayer = new Player(name: player.name)
			players << currentPlayer
		}
		println "on traite le joueur $player.name"
		def words = player.proposal.collect { word ->
			word = formatWord(word)
			def currentWord = allWordsOfADay.find { it.value == word }
			if(!currentWord){
				println "ajout du mot $word"
				currentWord = new Word(value: formatWord(word)) 
				allWordsOfADay << currentWord
			} else {
				println "le mot $word existe déjà, on incrémente les points"
				currentWord.points = currentWord.points == 0 ? 2 : currentWord.points + 1
			}
			currentWord
		}
		currentPlayer.proposal << new Proposal(day: day.index, words:words)
	}
	wordsPerDay.put(day.index, allWordsOfADay)
}

println players
println wordsPerDay


@ToString
class Player {
	String name
	List<Proposal> proposal = []
}

@ToString
class Proposal {
	int day
	List<Word> words
}

@ToString
class Word {
	String value
	int points = 0

	boolean equals(Object o){
		return o.value == this.value
	}

	int hashCode(){
		return value.hashCode()
	}
}

String formatWord(String word) {
	word = Normalizer.normalize(word.toUpperCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	word.trim().replaceAll('-', ' ')
}