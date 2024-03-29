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

tweets_filename = 'tweets'
trends_filename = 'trends'
processed_tweets_filename = 'tweets.tok'
processed_trends_filename = 'trends.tok'

parser = argparse.ArgumentParser(description='Tokenizes tweets and splits to days')
parser.add_argument('-v','--verbose', dest='verbose', help='Print verbose information', action='store_true', default=False, required=False)
args = vars(parser.parse_args())

print args['verbose']

#tweet file description
#line1: metaInfo
#line2: tweet

#(0)id|(1)user|(2)date|(3)isReply|(4)isRetweet|(5)retweetCount|(6)favoriteCount|(7)symbolList|(8)symbolCount|
#(9)hashtagList|(10)hashtagCount|(11)urlList|(12)urlCount|(13)mentionList|(14)mentionCount|(15)mediaTypeList|(16)mediaCount

fromSearchIndex = 0
idIndex = 1
userIndex = 2
dateIndex = 3
isReplyIndex = 4
isRetweetIndex = 5
retweetCountIndex = 6
favoriteCountIndex = 7
symbolListIndex = 9
symbolCountIndex = 9
hashtagListIndex = 10
hashtagCountIndex = 11
urlListIndex = 12
urlCountIndex = 13
mentionListIndex = 14
mentionCountIndex = 15
mediaTypeListIndex = 16
mediaCountIndex = 17


#print 'Input file:', args['input_filename']
#print 'Output file:', output_filename

#input_file = open(args['input_filename'], 'r')

processed_data_directory = 'ProcessedData'
data_directory = 'Data'
last_file = ""
tweet_set = set()

#For each location under data_directory
#locations = [x[0] for x in os.walk(data_directory)]

for location_name in listdir(data_directory):
	print location_name
	if location_name.startswith(".") or location_name=='README':
		continue;

	location_dir = pjoin(data_directory, location_name)
	processed_dir = pjoin(processed_data_directory, location_name)

	tweets_file = pjoin(location_dir, tweets_filename)
	trends_file = pjoin(location_dir, trends_filename)

	processed_tweets_file = pjoin(processed_dir, processed_tweets_filename)
	processed_trends_file = pjoin(processed_dir, processed_trends_filename)

	output_file = None
	print processed_tweets_file
	print processed_trends_file

	input_file = open(tweets_file, 'r')
	with input_file as input:
		count = 0
		while True:
			line1 = input.readline().decode('utf-8')
			line2 = input.readline().decode('utf-8')
			if not line2: break #EOF
			
			raw_tweet = line2
			fields = line1.replace('\n',' ').replace('\r',' ').replace('\r\n',' ').strip().split("|")
			
			tweetId = fields[idIndex]

			if (tweetId in tweet_set):
				continue
			tweet_set.add(tweetId)

			fromSearch = fields[fromSearchIndex]			
			userName = fields[userIndex]
			dateStr = fields[dateIndex]
			isReply = fields[isReplyIndex]
			isRetweet = fields[isRetweetIndex]
			retweetCount = fields[retweetCountIndex]
			favoriteCount = fields[favoriteCountIndex]
			symbolCount = fields[symbolCountIndex]
			urlsCount = fields[urlCountIndex]
			mediaCount = fields[mediaCountIndex]

			hashtagList = fields[hashtagListIndex]
			mentionList = fields[mentionListIndex]
			
			#(0)Fri (1)Mar (2)14 (3)14:32:04 (4)EET (5)2014
			dateFields = dateStr.split()
			day = dateFields[2]
			month = dateFields[1]
			time = dateFields[3]
			date = datetime.datetime.strptime(" ".join([day,month]),"%d %b")	
			directory = "-".join([str(date.day),str(date.month)])

			path_to_dir = pjoin(processed_dir, directory)
		
			if not os.path.exists(path_to_dir):
				os.makedirs(path_to_dir)

			path_to_file = pjoin(path_to_dir, processed_tweets_filename)

			#Close the old, open the new
			if path_to_file != last_file: 
				if output_file and not output_file.closed:
					output_file.close()
			print path_to_dir
			output_file = codecs.open(path_to_file, 'a', 'utf-8')
			print output_file
			last_file = path_to_file


			# Tokenize for hashtags/urls/@replies
			unit_tokenizer = RegexpTokenizer(r'\S+')		
			unit_tokens = unit_tokenizer.tokenize(line2)

			# Get hashtags
			hashtags = [t for t in unit_tokens if t.startswith('#')]

			# Get urls
			urls = [t for t in unit_tokens if (t.startswith('http:') or t.startswith('https:'))]

			# Get replies
			replies = [t for t in unit_tokens if t.startswith('@')] 

			# Remove hashtags, urls and @replies
			for reply in replies:
				line2 = line2.replace(reply, '')
			for url in urls:
				line2 = line2.replace(url, '')
			for hashtag in hashtags:
				line2 = line2.replace(hashtag, '')

			# Tokenize for tokens
			token_tokenizer = RegexpTokenizer(r'\w+')
			word_tokens = token_tokenizer.tokenize(line2)

			# Lowercase word tokens
			word_tokens = [t.lower() for t in word_tokens]

			# Lowercase urls and hashtags
			urls = [t.lower() for t in urls]
			hashtags = [t.lower() for t in hashtags]

			#tokens = [t.lower() for t in tokens if (not t.startswith('http:') and not t.startswith('@') and not t.startswith('#'))]

			# Remove stopwords
			word_tokens = [t for t in word_tokens if not t in stopwords.words('english')]
			raw_tweet = raw_tweet.replace('|',' ').replace('\n',' ').replace('\r',' ').replace('\r\n',' ')

			line = "|".join([fromSearch, tweetId, userName, time, isReply, isRetweet, retweetCount, favoriteCount, symbolCount, urlsCount, mediaCount, ",".join(word_tokens), hashtagList, mentionList, raw_tweet])
			output_file.write(line)
			output_file.write('\n')

			if args['verbose'] == True:
				print line1
				print line2
				print 'Tokens:', tokens
				print 'Hashtags:', hashtags
				print 'URLs:', urls
				print 'Replies:', replies
				print


for location_name in listdir(data_directory):
	if location_name.startswith(".") or location_name=='README':
		continue;
	location_dir = pjoin(data_directory, location_name)
	processed_dir = pjoin(processed_data_directory, location_name)

	tweets_file = pjoin(location_dir, tweets_filename)
	trends_file = pjoin(location_dir, trends_filename)

	processed_tweets_file = pjoin(processed_dir, processed_tweets_filename)
	processed_trends_file = pjoin(processed_dir, processed_trends_filename)

	output_file = None

	print location_name
	# Separate trends to days
	input_file = open(trends_file, 'r')
	with input_file as input:
		count = 0
		while True:
			# Read trends specification
			line1 = input.readline().decode('utf-8')
			if not line1: break #EOF

			# Find date (18/02/2014 17:39:00)
			fields = line1.split()
			date = fields[0]
			time = fields[1]

			dateFields = date.split("/")
			day = dateFields[0]
			month = dateFields[1]
			dateObj = datetime.datetime.strptime(" ".join([day,month]),"%d %m")	
			directory = "-".join([str(dateObj.day), str(dateObj.month)])	

			path_to_dir = pjoin(processed_dir, directory)

			if not os.path.exists(path_to_dir):
				os.makedirs(path_to_dir)

			path_to_file = pjoin(path_to_dir, processed_trends_filename)

			#Close the old, open the new
			if path_to_file != last_file: 
				if output_file and not output_file.closed:
					output_file.close()
				print path_to_dir
				output_file = codecs.open(path_to_file, 'a', 'utf-8')
				print output_file
				last_file = path_to_file

			output_file.write(line1)

			for count in range(0, 10):
				line1 = input.readline().decode('utf-8')
				if not line1: break #EOF
				output_file.write(line1)

































