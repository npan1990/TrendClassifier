#!/usr/bin/python

import os, sys
from os.path import join as pjoin
import codecs

vectors_directory = 'Vectors'
output_file = "trendVectors.csv"
output_file = codecs.open(output_file, 'w', 'utf-8')

header = "trend, trendClass, trendLength, relevantTweetsFromStream, tokensPerTweet, mentionsPerTweet, hashTagsPerTweet, tweetsWithUrl, tweetsWithReplies, tweetsWithRts, retweetsPerTweet, favoritesPerTweet, urlsPerTweet, mediasPerTweet, averageRank, mostDominantRank, maximumRank, mostDominantSlice, duration, durationOfLongestDateRange, daySlices, 1, 2, 3, 4, 5, 6, 7"
output_file.write(header)
output_file.write('\n')

#For each location under vectors_directory
for location_name in os.listdir(vectors_directory):
	print location_name
	if location_name.startswith(".") or location_name=='README':
		continue;

	location_dir = pjoin(vectors_directory, location_name)


	for date in os.listdir(location_dir):
		print "Location:", location_dir, " Date:", date

		date_dir = pjoin(location_dir, date)
		print "Date dir: ", date_dir

		vector_file = pjoin(date_dir, 'vector.csv')
		input_file = open(vector_file, 'r')
		with input_file as input:
			count = 0
			# skip header line
			header_line = input.readline()
			while True:
				line = input.readline().decode('utf-8')
				if not line: break #EOF

				fields = line.strip().split(",")

				trend = fields[0]
				trend = trend.replace(' ','_')
				trend = "_".join([trend, location_name, date]).replace("-","_")
				fields[0] = trend
				newline = ', '.join(fields)

				output_file.write(newline)
				output_file.write('\n')
				#print trend
output_file.close()
