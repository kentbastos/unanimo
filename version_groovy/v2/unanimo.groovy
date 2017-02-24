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

	day.players.each { inputPlayer ->
		def currentPlayer = players.find { it.name == inputPlayer.name }
		if(!currentPlayer) {
			//println "creation du joueur $inputPlayer.name"
			currentPlayer = new Player(name: inputPlayer.name)
			players << currentPlayer
		}
		//println "on traite le joueur $inputPlayer.name / propositions : $inputPlayer.proposals"
		def words = inputPlayer.proposal.collect { word ->
			word = formatWord(word)
			def currentWord = allWordsOfADay.find { it.value == word }
			if(!currentWord){
				//println "ajout du mot $word"
				currentWord = new Word(value: formatWord(word)) 
				allWordsOfADay << currentWord
			} else {
				//println "le mot $word existe déjà, on incrémente les points"
				currentWord.points = currentWord.points == 0 ? 2 : currentWord.points + 1
			}
			currentWord
		}
		currentPlayer.proposals << new Proposal(day: day.index, words:words, player: currentPlayer)
	}
	wordsPerDay.put(day.index, allWordsOfADay)
}

players.each { player ->
	player.proposals.each {
		it.check()
	}
}

// affichage
def outTemplateText = new File('.', 'resultats.template').text
def engine = new groovy.text.SimpleTemplateEngine()
def template = engine.createTemplate(outTemplateText).make(["players": players, "wordsPerDay":wordsPerDay])
def outputDir = "${System.getProperty('user.home')}/Downloads/"
def output = new File(outputDir, 'resultats_v2.html')
output.newWriter().withWriter { it << template.toString() }


@ToString
class Player {
	String name
	List<Proposal> proposals = []

	int getTotal() { proposals.inject(0){ acc, proposal -> acc + proposal.getPoints() } }
}

@ToString
class Proposal {
	int day
	List<Word> words
	Player player

	int getPoints() { words.inject(0) { acc, word -> acc + word.points } }

	boolean check() {
		if(words.size() != 8) {
			println "nombre de mots invalides pour la proposition du jour $day du joueur $player.name"
		}
		if(words.size() != words.toSet().size()){
			println "présence de doublons dans la proposition du jour $day du joueur $player.name"
		}
	}
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