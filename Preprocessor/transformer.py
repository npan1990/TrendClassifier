#!/usr/bin/python

import os, sys
from os.path import join as pjoin
import codecs
from os import listdir
import datetime
import json
import nltk
from nltk.corpus import stopwords
from nltk.corpus import wordnet
from nltk.tokenize.punkt import PunktWordTokenizer
from nltk.tokenize import RegexpTokenizer

files_directory = 'R'
output_directory = 'ProcessedDataset'
output_filename = "tweets"
output_file = codecs.open(output_filename, 'w', 'utf-8')

header = "trend, trendClass, trendLength, relevantTweetsFromStream, tokensPerTweet, mentionsPerTweet, hashTagsPerTweet, tweetsWithUrl, tweetsWithReplies, tweetsWithRts, retweetsPerTweet, favoritesPerTweet, urlsPerTweet, mediasPerTweet"


last_file = ""
file_count = 0
#For each .json file under files_directory
for filename in os.listdir(files_directory):
	
	if not ".json" in filename:
		continue
	file_count+= 1
	#Get date from filename 'all_uk-1054-2014_02_16_13_45.json'
	date = filename.split("-")[2]	
	date_fields = date.split("_")
	month = date_fields[1]
	day = date_fields[2]
	#date = datetime.datetime.strptime(" ".join([day,month]),"%d %b")	
	directory = "-".join([str(day),str(month)])
	
	path_to_dir = pjoin(output_directory, directory)

	
	#if not os.path.exists(path_to_dir):
	#	os.makedirs(path_to_dir)
	
	#path_to_file = pjoin(path_to_dir, output_filename)
	#print 'File: ', path_to_file

	#Close the old, open the new
	#if path_to_file != last_file: 
		#if output_file and not output_file.closed:
			#output_file.close()
	#output_file = codecs.open(path_to_file, 'a', 'utf-8')
	#last_file = path_to_file

	#Start reading the .json file
	
	tweets_file = pjoin(files_directory, filename)
	input_file = open(tweets_file, 'r')
	#print input_file
	with input_file as input:
		count = 0
		while True:
			line = input.readline().decode('utf-8')
			if not line: break #EOF
			count+=1
			#print input_file, ' line: ', count, ' fileCount: ', file_count
			try:
				tweet = json.loads(line)
			except ValueError, e:
				continue
		
			#(0)Fri (1)Mar (2)14 (3)14:32:04 (4)EET (5)2014
			try:
				dateStr = tweet["created_at"]		
			except KeyError, e:
				continue

			dateFields = dateStr.split()
			day = dateFields[2]
			month = dateFields[1]
			time = dateFields[3]
			date = datetime.datetime.strptime(" ".join([day,month]),"%d %b")	
			directory = "-".join([str(date.day),str(date.month)])

			path_to_dir = pjoin(output_directory, directory)
		
			if not os.path.exists(path_to_dir):
				os.makedirs(path_to_dir)

			path_to_file = pjoin(path_to_dir, output_filename)

			#Close the old, open the new
			if path_to_file != last_file: 
				if output_file and not output_file.closed:
					output_file.close()
			#print path_to_dir
			output_file = codecs.open(path_to_file, 'a', 'utf-8')
			#print output_file
			last_file = path_to_file

			fromSearch = "0"
			if 'id_str' not in tweet:
				continue

			tweetId = tweet["id_str"]	
			userName = tweet["user"]["screen_name"]	
			userId = tweet["user"]["id"]

			userVerified = -1
			if 'verified' in tweet["user"]:
				userVerified = tweet["user"]["verified"]
			
			userFollowersCount = -1
			if 'followers_count' in tweet["user"]:
				userFollowersCount = tweet["user"]["followers_count"]
			
			userFriendsCount = -1			
			if 'friends_count' in tweet["user"]:
				userFriendsCount = tweet["user"]["friends_count"]
			
			userListedCount = -1
			if 'listed_count' in tweet["user"]:
				userListedCount = tweet["user"]["listed_count"]
			
			userStatusesCount = -1
			if 'statuses_count' in tweet["user"]:
				userStatusesCount = tweet["user"]["statuses_count"]
			
				
			inReplyToUserId = tweet["in_reply_to_user_id"]
			isRetweet = tweet["retweeted"]
			retweetCount = tweet["retweet_count"]			
			favoriteCount = tweet["favorite_count"]
			entities = tweet["entities"]
			
			hashtags = []
			if 'hashtags' in entities:
				hashtags_list = entities['hashtags']
				for hashtag in hashtags_list:
					hashtags.append(hashtag['text'])
			
			urls = []
			if 'urls' in entities:
				url_list = entities['urls']
				for url in url_list:
					urls.append(url['expanded_url'])

			user_mentions = []
			if 'user_mentions' in entities:
				user_mentions_list = entities['user_mentions']	
				for um in user_mentions_list:
					user_mentions.append(str(um['id']))

			media = []
			if 'media' in entities:
				media_list = entities['media']
				for medium in media_list:
					media.append(medium['type'])

			tweet_text = tweet["text"]
			raw_tweet = tweet_text
			#Tokenize raw tweet
			# Tokenize for hashtags/urls/@replies
			unit_tokenizer = RegexpTokenizer(r'\S+')		
			unit_tokens = unit_tokenizer.tokenize(tweet_text)

			# Get hashtags
			my_hashtags = [t for t in unit_tokens if t.startswith('#')]

			# Get urls
			my_urls = [t for t in unit_tokens if (t.startswith('http:') or t.startswith('https:'))]

			# Get replies
			my_mentions = [t for t in unit_tokens if t.startswith('@')] 

			# Remove hashtags, urls and @replies
			for mention in my_mentions:
				raw_tweet = raw_tweet.replace(mention, '')
			for url in my_urls:
				raw_tweet = raw_tweet.replace(url, '')
			for hashtag in my_hashtags:
				raw_tweet = raw_tweet.replace(hashtag, '')

			# Tokenize for tokens
			token_tokenizer = RegexpTokenizer(r'\w+')
			word_tokens = token_tokenizer.tokenize(raw_tweet)

			# Lowercase word tokens
			word_tokens = [t.lower() for t in word_tokens]

			# Remove stopwords
			word_tokens = [t for t in word_tokens if not t in stopwords.words('english')]
			raw_tweet = tweet_text.replace('|',' ').replace('\n',' ').replace('\r',' ').replace('\r\n',' ')
			
			newline = "|".join([fromSearch, str(tweetId), userName, str(userId), str(userVerified), str(userFollowersCount), str(userFriendsCount), str(userListedCount), str(userStatusesCount), dateStr, str(inReplyToUserId), str(isRetweet), str(retweetCount), str(favoriteCount), ",".join(hashtags), str(len(hashtags)), ",".join(urls), str(len(urls)), ",".join(user_mentions), str(len(user_mentions)), ",".join(media), str(len(media)), ",".join(word_tokens), raw_tweet])
			
			
			#print 'Symbols: ', symbols
			#print 'Hashtags: ', hashtags
			#print 'Urls: ', urls
			#print 'Media: ', media
			#print 'User mentions: ', user_mentions
			#print tweet_text

			output_file.write(newline)
			output_file.write('\n')
			#output_file.write(tweet_text)
			#output_file.write('\n')
		#print 'Found ', count, ' tweets'

#q			metaline = "|".join(


