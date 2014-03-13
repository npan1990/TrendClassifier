import sys
import nltk
import datetime
import argparse
from nltk.corpus import stopwords
from nltk.corpus import wordnet
from nltk.tokenize.punkt import PunktWordTokenizer
from nltk.tokenize import RegexpTokenizer
from os.path import join as pjoin
import os
from os import listdir
import codecs

tweets_filename = 'tweets.tok'
output_filename = 'unique_tweets.tok'
output_file = codecs.open(output_filename, 'a')
tweetid_set = set()
tweet_set = set()
#For each location under data_directory
#locations = [x[0] for x in os.walk(data_directory)]

with open(tweets_filename, 'r') as f:
	for line in f:
		fields = line.split("|")
		tweetId = fields[1]
		if tweetId not in tweetid_set:
			tweet_set.add(line)
			tweetid_set.add(tweetId)

print len(tweet_set)
print len(tweetid_set)

for tweet in tweet_set:
	output_file.write(tweet)


