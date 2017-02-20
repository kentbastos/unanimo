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
		currentPlayer.proposalsPerDay.put(dayIndex.toInteger(), allWords.collect { new Word(value: it.trim()) })
		maxDay = Math.max(maxDay, dayIndex.toInteger())
	}
	players << currentPlayer
}

println "> lecture des données ok"

checkInput(players)

// TODO check des doublons et de la taille de données

// calcul de la liste des mots et des points associés
def pointsForWordsOfDays = [:] // clé = jour / valeur = liste de Word
(0..maxDay).each { day -> pointsForWordsOfDays.put(day, computePointsForWordsOfDay(day, players)) }

println "> calcul des points par mot pour chaque jour ok"

// affectation des points des joueurs
computePointsForPlayers(players, pointsForWordsOfDays)
println "> calcul des points pour les joueurs ok"

// affichage
def outTemplateText = new File('.', 'resultats.template').text
def engine = new groovy.text.SimpleTemplateEngine()
def template = engine.createTemplate(outTemplateText).make(["maxDay": maxDay, "pointsForWordsOfDays": pointsForWordsOfDays, "players": players.sort {a,b -> b.totalPoints <=> a.totalPoints}])
def output = new File('.', 'output.html')
output.newWriter().withWriter { it << template.toString() }

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
	
	wordsForDay.sort { a,b -> b.points <=> a.points }
}

def computePointsForPlayers(List<Player> players, Map pointsForWordsOfDays){
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
}

def checkInput(List<Player> players){
	println "> vérification des données"
	players.each { player ->
		player.proposalsPerDay.each { day, words ->
			if(words.toSet().size() != words.size()){
				println "/!\\ WARN : le joueur $player.name a des doublons pour le jour $day"
			}
			if(words.size() != 8) {
				println "/!\\ WARN : le joueur $player.name a moins de 8 mots pour le jour $day"	
			}
		}
	}
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

	boolean equals(Object o){
		return o.value == this.value
	}

	int hashCode(){
		return value.hashCode()
	}
}