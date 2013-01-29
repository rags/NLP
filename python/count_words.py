import nltk
import json

def good_word(w):
    if w[0] > 0.5 or w[1] > 0.5:
        return True
    return False

def print_score(senti_words):
    f = open('yelp_reviews_3.txt')
    write = open('senti_score.csv','w')
    j = json.load(f)
    neg_count = 0
    total_neg_count = 0
    neg_count_2 = 0
    pos_count = 0
    pos_count_2 = 0
    total_pos_count = 0
    for rest in j:
        for review in j[rest]:
            words = nltk.word_tokenize(review['text'])
            pos = 0
            neg = 0
            count = 0
            pos_counter = 0
            neg_words = set()
            pos_words = set()
            for word in set(words):
                w = word.lower()
                if w in senti_words:
                    if senti_words[w][0] > 0.7:
                        pos_words.add(w)
                        pos += senti_words[w][0]
                        pos_counter += 1
                    if senti_words[w][1] > 0.7:
                        neg_words.add(w)
                        neg += senti_words[w][1]
                        count += 1
            #neg = neg/count if count > 0 else 0
            #pos = pos/pos_counter if pos_counter > 0 else 0
            if review['sentiment'] == "N":
                total_neg_count += 1
                if neg > 3 and pos-neg < 0:
                    neg_count += 1
                if pos-neg > 3:
                    neg_count_2 += 1
                '''
                if (pos - neg) < -1:
                    neg_count += 1
                if (pos - neg) > 0:
                    print review
                    neg_count_2 += 1
                '''
            if review['sentiment'] == "P":
                total_pos_count += 1
                '''
                if pos > 6:
                    pos_count += 1
                if neg > 6:
                    pos_count_2 += 1
                '''
                if  pos-neg  > 3:
                    pos_count += 1
                if  neg > 3 and pos-neg < 0:
                    print review, pos, neg,pos_words, neg_words
                    pos_count_2 += 1
            write.write(review['sentiment']+", "+ str(pos - neg)+"\n")
    print float(neg_count)/(neg_count + pos_count_2), float(neg_count)/total_neg_count
    print float(pos_count)/(pos_count + neg_count_2),float(pos_count)/total_pos_count
    '''
    print float(neg_count)/total_neg_count
    print float(neg_count_2)/total_neg_count
    print float(pos_count)/total_pos_count
    print float(pos_count_2)/total_pos_count
    '''
    write.close()


if __name__== "__main__":
    f = open('senti_words.txt')
    word_dict = {}
    for line in f:
        word_data = line.split('\t')
        words = word_data[4].split(" ")
        for word in words:
            ind = word.find('#')
            word_dict[word[:ind]] = [float(word_data[2]), float(word_data[3])]
    print_score(word_dict)

