tweets_filename = 'tweets.tok'
tweet_id_set = set()

with open(tweets_filename, 'r') as f:
	for line in f:
		fields = line.split('|')
		tweet_id = fields[1]
		if tweet_id not in tweet_id_set:
			tweet_id_set.add(tweet_id)

print len(tweet_id_set)

