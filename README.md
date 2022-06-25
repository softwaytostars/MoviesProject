## Instructions for running the program

1) Make sure java >=11 is installed
2) You can either use maven to build the project :

`mvn clean install`

`java -jar target/MovieProject-1.0-SNAPSHOT.jar ../dedup-2020/movies.tsv`

3) or build it manually:

`javac -d classes src/main/java/test/contentsquare/*.java src/main/java/test/contentsquare/dataprovider/*.java src/main/java/test/contentsquare/compatibility/*.java src/main/java/test/contentsquare/algo/*.java src/main/java/test/contentsquare/domain/*.java`

`java -cp ./classes test.contentsquare.Main your_path_to_csv `

4) or Run the Main class with your favorite IDE.

## Some code explanations

The main class to execute is test.contentsquare.Main.
It uses the `MovieDataProvider` object for reading the input file and creating a list of `Movie` objects with the right properties.

Then, the `MovieDuplicateFinder` object is used to find the matches from the list of movies.
It writes the results directly in the "output.txt" file thanks to the writer.

The algo checks matches concurrently for each movie. I had a first version where it was done synchronously but the time 
was much more 20 minutes (cf I kept the commit for reference).
The algo, in the worst case (all same dates, no matching) is in O(N2).
It behaves better with uniformed years for movies like in the input data sample. 
With concurrency, it runs in about 1 minute.

The algo is based on the sort of the movie list, by year. That's what I would do manually to check with my own eyes.
Then, for each movie in the list, I compare it to the next elements in the year+1 range and filter on the length distance.
These are candidates on which to check if we can conclude a match.
Actually, it's more a guess, based on home made 'rules' on the fields genre, director and actors.
I will explain the rules below. 
These rules are encapsulated in `WeightStrategyCompatibility` class that implements the interface `StrategyCompatibility`.
Indeed, whether the year and length are necessary conditions, we could want to change dynamically the rules strategy that decide if
a movie is a match with another.
`WeightStrategyCompatibility` is the default implementation used by the algorithm.
Another class `BasicStrategyCompatibility` was first used. It's possible to check what it gives running
`java -cp ./classes test.contentsquare.Main basic` (thanks to the stragegy factory `FactoryStrategy`) but my favorite choice 
was `WeightStrategyCompatibility`.
There are less matches, with this implementation, so it doesn't help K/M but I'm more confident to have a better K/N.
Howhever, I relaxed the conditions so that K/M could not be too bad.

I chose to calculate a score value depending on the fields. In the case of `WeightStrategyCompatibility`, that's more a probability of match.
I defined an 'empirical' threadshold of 65% under which the score is considered as zero. So, to not consider as a match.

Among many candidates for matches against a movie, the match is chosen for the highest candidate score.

### Rules in the strategy

- If there is only the genre, I score to zero. Because, I would not trust 2 movies to be the same with only the genre.

- If there are no actors to compare to, but directors, then at least, there should be >= 2 directors in each to compare
  Making the assumption that the probability that 2 same directors make together a second movie in 1 year range should be small.
  That risks to lower K/M but have a better K/N.

- If there are no common actors, that's difficult to tell. At least require 2 same directors. 
  The assumption is that having 2 same directors in different movies in 1 year range should be small.
  That risks to lower K/M but have a better K/N.

- Then I compute a weighted ratio. `genre` is weighted 1, `directors` weighted 2 and `actors` weighted 3.
  Indeed it seems to me that `genre` is an additional information to take in account but less relevant than the `directors` and the `actors`.
  The `directors` field is an important information but most of the time there is only one director (that could be missing).
  The ratio is the number of people in common for a given field (ex: N actors in common) divided by the union of the field values 
  (ex: D actors is the union of the 2 sets of actors). 
  To reflect the fact that a ratio of 100% with a lot of people is not the same information than a ration 100% with few people,
  I decided to multiply the weight by the number of people in common (and to compute the barycenter accordingly).

- Then if the score computed (to be compared to a probability) is less than 65%, I chose to score to zero.

With a neural network library and a set of data with right answers and wrong answers, it would have been interesting to give a try.

