# Unanimo - Groovy script

The script helps to compute the points for unanimo, if you play with people by email.

## HOW TO

* spécify the output directory in the variable "outputDir"
* Filled the data in the input.json file. It contains a list of players and their proposals for each day.
* Run the script with the command : groovy unanimo.groovy
* The output is an html file named unanimo.html.

## Details

The script transforms each word : it removes the accents and put the word in uppper case.

Some checks are done : The duplicates and the number of proposals. See the console output to see if there is any error.