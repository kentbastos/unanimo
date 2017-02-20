import groovy.json.JsonSlurper
import groovy.transform.ToString
import java.text.Normalizer

def input = new File ('.', 'input.json')
def jsonSlurper = new JsonSlurper()
def inputData = jsonSlurper.parseText(input.text)

List<Player> players = []
int maxDay = Integer.MIN_VALUE

// Parsing de l'input
inputData.players.each { player ->
	def currentPlayer = new Player(name: player.name)
	def playerProposals = []
	player.proposals.each { dayIndex, allWords ->
		allWords = allWords.collect { 
			Normalizer.normalize(it.toUpperCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		}
		currentPlayer.proposalsPerDay.put(dayIndex.toInteger(), allWords.collect { new Word(value: it) })
		maxDay = Math.max(maxDay, dayIndex.toInteger())
	}
	players << currentPlayer
}

println "lecture des données ok"

// calcul de la liste des mots et des points associés
def pointsForWordsOfDays = [:] // clé = jour / valeur = liste de Word
(0..maxDay).each { day -> pointsForWordsOfDays.put(day, computePointsForWordsOfDay(day, players)) }

println "calcul des points par mot pour chaque jour ok"

// affectation des points des joueurs
players.each { player ->
	player.proposalsPerDay.each { day, playerWords ->
		def wordsOfDay = pointsForWordsOfDays[day]
		def totalForDay = 0
		playerWords.each { playerWord ->
			def wordOfDay = wordsOfDay.find { it.value == playerWord.value }
			if(wordOfDay){
				playerWord.points  = wordOfDay.points
				totalForDay += playerWord.points
			} 
		}
		player.pointsPerDay.put(day, totalForDay)
	}
}
println "calcul des points pour les joueurs ok"

// affichage
// TODO : génration output via template
pointsForWordsOfDays.each { day, words ->
	println "pour le jour $day les mots sont $words"
}

players.each { player ->
	//println player
	player.pointsPerDay.each { day, points ->
		println "Pour le jour $day le joueur $player.name a $points points"
	}
	println "le joueur $player.name a $player.totalPoints points au total"
}

/**************************************************************************************************************
*                                                  METHODES ET CLASSES
**************************************************************************************************************/

List<Word> computePointsForWordsOfDay(int day, List<Player> players) {
	def wordsForDay = []
	players.each { player ->
		def playerProposals = player.proposalsPerDay.find { it.key == day }.value
		playerProposals.each { playerWord ->
			def sameWord = wordsForDay.find { it.value == playerWord.value }
			if(!sameWord){
				sameWord = new Word(value: playerWord.value)
				wordsForDay << sameWord
			} else {
				sameWord.points = sameWord.points == 0 ? 2 : sameWord.points + 1
			}
		}
	}
	
	wordsForDay
}

@ToString
class Player {
	String name
	Map<Integer, List<Word>> proposalsPerDay = [:]
	Map<Integer, Integer> pointsPerDay = [:]
	
	int getTotalPoints(){
		pointsPerDay.values().inject(0) {acc, value -> acc + value}
	}
}

@ToString
class Word {
	String value
	Integer points = 0
}