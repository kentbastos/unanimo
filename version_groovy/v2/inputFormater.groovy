def allPlayersWordsForADay = """24/02/2017 09:52:18,slip,animal,australie,saut,box,poche,steak,bush,cedric.linez@cbp-group.com
24/02/2017 09:52:33,Australie,Poche,Saut,boxe,walibi,aborigène,sac,animal,heloise.duplouy@cbp-group.com
24/02/2017 09:54:35,AUSTRALIE,POCHE,SAUT,BOXE,MARSUPIAL,DESERT,SKIPPY,WALLABY,flavien.barthe@cbp-group.com
24/02/2017 10:05:05,AUSTRALIE,BOOMERANG,STEAK,SAUT,BOXE,ANIMAL,POCHE,BOND,eric.vepa@cbp-group.com
24/02/2017 10:16:28,Skippy,Australie,Boxe,Saut,Viande,Poche,Bonds,Vitesse,matthias.david@cbp-group.com
24/02/2017 10:20:40,AUSTRALIE,POCHE,SAUT,MARSUPIAL,BOXE,WALLABY,ANIMAL,BEBE,catalin.costea@cbp-group.com
24/02/2017 10:59:44,AUSTRALIE,BOXE,POCHE,ANIMAL,SAUT,WALIBI,MARSUPIAL,BOND,quentin.moquay@cbp-group.com"""

allPlayersWordsForADay.eachLine { line ->
	def tokens = line.tokenize(',')
	def playerInput =  '{"name":"' + tokens[-1] +  '", "proposal":['
	(1..8).each {
		playerInput <<= '"' + tokens[it] + '"'
		if(it != 8) {
			playerInput << ','
		}
	}
	playerInput << ']},'
	println playerInput
}