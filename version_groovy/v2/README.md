This version implements an other input format. The players enter the words in a google forms, and the data are retrieved via an excel sheet. It's quicker.

## how to

* create a google form with the 8 questions
* read the response with the spreadsheet and export it as a csv file
* paste the data in the inputFormater.groovy file (remove the header first)
* run the script : groovy inpuFormater.groovy
* copy the output and paste it in the input.json file
* run the unanimo.groovy file and voila