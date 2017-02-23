def allPlayersWordsForADay = """23/02/2017 09:01:31,FEU,MARTEAU,TAPIS,VIN,COULEUR,SANG,STOP,ROSE,flavien.barthe@cbp-group.com
23/02/2017 09:14:41,sang,marteau,vif,feu,communiste,russe,vin,viande,cedric.linez@cbp-group.com
23/02/2017 09:20:15,sang,bleu,blanc,tomate,poivron,colère,noir,ferrari,heloise.duplouy@cbp-group.com
23/02/2017 09:28:57,NOIR,BLEU,SANG,POURPRE,SOCIALISTE,VIF,RAGE,COULEUR,catalin.costea@cbp-group.com
23/02/2017 09:55:41,TAUREAU,SANG,VIN,URSS,POISSON,NEZ,CHINE,VIANDE,quentin.moquay@cbp-group.com
23/02/2017 10:11:38,BLEU,VERT,STOP,SANG,FRAISE,CERISE,PRIMAIRE,COULEUR,eric.vepa@cbp-group.com
23/02/2017 11:17:58,Camion,Pompiers,Sang,Ecarlate,Cerise,Communisme,Colère,Pomme,matthias.david@cbp-group.com
23/02/2017 11:50:26,peau,colère,sang,couleur,crayon,peinture,ferrari,voiture,nicolas.jajolet@cbp-group.com"""

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