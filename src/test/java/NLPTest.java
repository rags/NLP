import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.doccat.*;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Strings.padStart;
import static com.google.common.collect.Iterators.filter;
import static com.google.common.collect.Iterators.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.io.ByteStreams.newInputStreamSupplier;
import static com.google.common.io.CharStreams.newReaderSupplier;
import static com.google.common.primitives.Doubles.asList;
import static java.lang.String.*;
import static java.lang.String.format;

public class NLPTest {

    public static final int INT = 7000;

    public static void main(String[] args) {
        System.out.print("d");
    }

    @Test
    public void tokenizerTest() throws Exception {
        final TokenizerME tokenizer = tokenizer();
        String[] tokenize = tokenizer.tokenize("I cannot say enough about the amazing lobster risotto.");
        p(tokenize);
        tokenize = tokenizer.tokenize("I do not understand this shit. I do this. I do that");
        p(tokenize);

        double[] tokenProbabilities = tokenizer.getTokenProbabilities();
        for (int i = 0, tokenProbabilitiesLength = tokenProbabilities.length; i < tokenProbabilitiesLength; i++) {
            System.out.println(tokenize[i] + " " + tokenProbabilities[i]);
        }

    }

    private TokenizerME tokenizer() throws IOException {
        return new TokenizerME(new TokenizerModel(new FileInputStream("/Users/rags/Downloads/en-token.bin")));
    }

    private <T> void p(T[] a) {
        for (T s : a) {
            System.out.println(s);
        }
    }

    @Test
    public void pos() throws Exception {
        final POSTagger posTagger = posTagger();
        final String[] tokens =
                tokenizer().tokenize("Time files like an arrow. Fruit flies like an Apple");
        final String[] tags = posTagger.tag(tokens);

        for (int i = 0; i < tags.length; i++)
            System.out.println(tokens[i] + " " + tags[i]);
        final Sequence[] sequences = posTagger.topKSequences(tokens);
        for (Sequence s : sequences) {
            p(s);
        }

    }

    private POSTaggerME posTagger() {
        try {
            return new POSTaggerME(new POSModel(new FileInputStream("/Users/rags/Downloads/en-pos-maxent.bin")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void chunker() throws Exception {
        final ChunkerModel chunkerModel = new ChunkerModel(new FileInputStream("/Users/rags/Downloads/en-chunker.bin"));
        final ChunkerME chunker = new ChunkerME(chunkerModel);
        final String[] tokens = tokenizer().tokenize("I cannot say enough about the amazing lobster risotto.");
        final POSTaggerME posTaggerME = posTagger();
        final String[] tags = posTaggerME.tag(tokens);

        final String[] chunk = chunker.chunk(tokens, tags);

        final Joiner joiner = Joiner.on(", ");
        p("==========");
        p(joiner.join(tags));
        p(joiner.join(tokens));
        p(joiner.join(chunk));
        Span[] chunkAsSpans = chunker.chunkAsSpans(tokens, tags);
        final String[] parts = Span.spansToStrings(chunkAsSpans, tokens);
        for (int i = 0, chunkAsSpansLength = chunkAsSpans.length; i < chunkAsSpansLength; i++) {
            System.out.println(parts[i] + " = " + chunkAsSpans[i].getType());
        }

        p("==========");
        p(joiner.join(asList(posTaggerME.probs())));
        p(joiner.join(asList(posTaggerME.probs())));


    }

    @Test
    public void ngram() throws Exception {
        final NGramFeatureGenerator generator = new NGramFeatureGenerator();
//        final BagOfWordsFeatureGenerator generator = new BagOfWordsFeatureGenerator();
        final Collection<String> strings = generator.extractFeatures(new String[]{"I", "Think", ",", "But", "I", "Am", "not", "sure", "that", "Aadi", "is", "an", "Idiot"});
        System.out.print(strings);
    }

    /*,
                    new FeatureGenerator() {
                        final POSModel posModel = new POSModel(new FileInputStream("/Users/rags/Downloads/en-pos-maxent.bin"));
                        final POSTagger posTagger = new POSTaggerME(posModel);
                        @Override
                        public Collection<String> extractFeatures(String[] text) {
                            posTagger.tag(text);
                            final Sequence[] sequences = posTagger.topKSequences(text);
                            final HashMap<String, Integer> stringHashMap = new HashMap<String, Integer>();
                            for(String word :text){
                                word = word.toLowerCase();
                                int count = stringHashMap.containsKey(word)? stringHashMap.get(word) : 0;
                                stringHashMap.put(word, count + 1);
                            }
                            p(stringHashMap);
                            return new ArrayList<String>();

                        }
                    }*/
    @Test
    public void __() {
        System.out.println("[" + padEnd(padStart("X", 6, ' '), 7, ' ') + "]");
    }

    @Test
    public void testParsingRating() throws Exception {

        final DoccatModel doccatModel = DocumentCategorizerME.train("en",
                new DocumentSampleStream(
                        new PlainTextByLineStream(new FileInputStream("/Users/rags/yelp_model_rating"), "UTF-8")
                ));

        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(doccatModel,
                new NGramFeatureGenerator(),
                new BagOfWordsFeatureGenerator());

        System.out.print(doccatModel.getChunkerModel());
        System.out.print(myCategorizer.getNumberOfCategories());

        int count = 0, failureCount = 0;

        for (ArrayList<Map<String, String>> restaurant : json("/Users/rags/Downloads/yelp_ratings_json-2").values()) {
            for (Map<String, String> tip : restaurant) {
                count++;
                if (count <= INT) {
                    continue;
                }
                double[] outcomes = myCategorizer.categorize(tip.get("text"));
                String category = myCategorizer.getBestCategory(outcomes);
                if (!valueOf(tip.get("rating")).equals(category)) {
                    failureCount++;
                    System.out.println(format("Expected %s but was %s for %s", tip.get("rating"), category, tip.get("text")));
                }
            }
        }

        System.out.println(
                format("%d out of %d failed", failureCount, count - INT));
    }

    @Test
    public void testSentimentBasedOnRating() throws Exception {

        final DoccatModel doccatModel = DocumentCategorizerME.train("en",
                new DocumentSampleStream(
                        new PlainTextByLineStream(new FileInputStream("/Users/rags/yelp_model_rating"), "UTF-8")
                ));

        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(doccatModel,
                new NGramFeatureGenerator(),
                new BagOfWordsFeatureGenerator());

        System.out.print(doccatModel.getChunkerModel());
        System.out.print(myCategorizer.getNumberOfCategories());

        int count = 0, failureCount = 0;

        for (ArrayList<Map<String, String>> restaurant : json("/Users/rags/Downloads/yelp_ratings_json-2").values()) {
            for (Map<String, String> tip : restaurant) {
                count++;
                if (count <= INT) {
                    continue;
                }
                double[] outcomes = myCategorizer.categorize(tip.get("text"));
                final double rating = Double.parseDouble(myCategorizer.getBestCategory(outcomes));
                String sentiment = rating > 3 ? "P" : rating < 3 ? "N" : "U";
                if (!tip.get("sentiment").equals(sentiment)) {
                    failureCount++;
                    System.out.println(format("Expected %s but was %s for %s", tip.get("sentiment"), sentiment, tip.get("text")));
                }
            }
        }

        System.out.println(
                format("%d out of %d failed", failureCount, count - INT));
    }

    @Test
    public void testParsingSentiment() throws Exception {

        foo("/Users/rags/yelp_%s_sentiment", new NGramFeatureGenerator(),
                new BagOfWordsFeatureGenerator());
    }

    @Test
    public void testSentiment_adjectives() throws Exception {

        foo("/Users/rags/yelp_%s_sentiment",
                // new BagOfWordsFeatureGenerator(),
                //new NGramFeatureGenerator(),
                new FeatureGenerator() {
                    final POSTaggerME posTaggerME = posTagger();

                    @Override
                    public Collection<String> extractFeatures(String[] text) {
                        final String[] tags = posTaggerME.tag(text);
                        final ArrayList<String> adjectives = new ArrayList<String>();
                        for (int i = 0; i < tags.length; i++) {
                            if (tags[i].startsWith("JJ") || tags[i].startsWith("NN") || tags[i].startsWith("R")) {
                                adjectives.add(text[i]);
                            }
                        }
                        //System.out.println("adjectives = " + adjectives);
                        return adjectives;
                    }

                    public String toString() {
                        return "Adjectives, nouns, Adverbs";
                    }
                });
    }

    boolean DEBUG = false;

    private void foo(String inputfileFormat, final FeatureGenerator... features) throws IOException {
        final DoccatModel doccatModel = DocumentCategorizerME.train("en",
                new DocumentSampleStream(
                        new PlainTextByLineStream(new FileInputStream(format(inputfileFormat, "model")), "UTF-8")
                ), 0, 100, features);

        final DocumentCategorizerME myCategorizer = new DocumentCategorizerME(doccatModel, features);


        //System.out.print(doccatModel.getChunkerModel());
        //System.out.print(myCategorizer.getNumberOfCategories());


        final LineProcessor<Object> processor = new LineProcessor<Object>() {
            String[] CATEGORIES = {"P", "N", "U"};
            Map<String, Map<String, Integer>> confusionMatrix = new HashMap<String, Map<String, Integer>>() {{
                for (String category : CATEGORIES) {
                    put(category, new HashMap<String, Integer>() {{
                        for (String cat : CATEGORIES) {
                            put(cat, 0);
                        }
                    }});
                }


            }};
            int count = 0
                    ,
                    failureCount = 0;

            @Override
            public boolean processLine(String line) throws IOException {
                count++;
                String expected = line.substring(0, 1);
                final String review = line.substring(2);
                double[] outcomes = myCategorizer.categorize(review);
                String actual = myCategorizer.getBestCategory(outcomes);
                final Map<String, Integer> category_row = confusionMatrix.get(expected);
                category_row.put(actual, category_row.get(actual) + 1);
                if (!expected.equals(actual)) {
                    failureCount++;
                    if (DEBUG) {
                        System.out.println(format("Expected %s but was %s for %s", expected, actual, review));
                    }
                }
                return true;
            }

            @Override
            public Object getResult() {
                System.out.println("Features: " + transform(Lists.newArrayList(features), new Function<FeatureGenerator, String>() {
                    @Override
                    public String apply(FeatureGenerator input) {
                        return input.toString();
                    }

                }));
                System.out.println(
                        format("%d out of %d failed. Sucess rate: %f", failureCount, count, (count - failureCount) * 1.0 / count));


                System.out.println(format("%4s |%7s |%7s |%7s", "", "N", "P", "U"));
                System.out.println(Strings.repeat("-", 35));
                final Map<String, Integer> N = confusionMatrix.get("N");
                System.out.println(format("%4s |%7d |%7d |%7d", "N", N.get("N"), N.get("P"), N.get("U")));
                final Map<String, Integer> P = confusionMatrix.get("P");
                System.out.println(format("%4s |%7d |%7d |%7d", "P", P.get("N"), P.get("P"), P.get("U")));
                final Map<String, Integer> U = confusionMatrix.get("U");
                System.out.println(format("%4s |%7d |%7d |%7d", "U", U.get("N"), U.get("P"), U.get("U")));

                return null;
            }
        };
        Files.readLines(new File(format(inputfileFormat, "test")), Charset.defaultCharset(), processor);

    }

    private void p(Object s) {
        System.out.println(s);
    }

    public Map<String, ArrayList<Map<String, String>>> json(String path) {
        try {
            return new ObjectMapper().readValue(
                    new File(path),
                    Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void modelFSTips() throws IOException {

        final BufferedWriter bufferedWriter = Files.newWriter(new File("/Users/rags/yelp_model_rating"), Charset.defaultCharset());
        int count = 0;
        for (ArrayList<Map<String, String>> restaurant : json("/Users/rags/Downloads/yelp_ratings_json-2").values()) {
            for (Map<String, String> tip : restaurant) {
                count++;
                if (count > 800) {
                    break;
                }
                bufferedWriter.append(tip.get("sentiment")).append(" ").append(tip.get("text").toLowerCase());
                bufferedWriter.newLine();
            }
        }
        bufferedWriter.close();
        System.out.print(Files.readLines(new File("/Users/rags/model"), Charset.defaultCharset()));
    }

    @Test
    public void _() {
        String x = "dgdf1.\nsdgdrsh2.\rfdshfd3. dskjhgfsdkjgh1\ndhgjsdhgdksj2\r";
        System.out.println(x);
        System.out.println(x.replaceAll("\\.[\r\n]", " ").replaceAll("[\r\n]", " "));

    }


    @Test
    public void modelYelp() throws IOException {

        final BufferedWriter sentimentWriter = Files.newWriter(new File("/Users/rags/yelp_model_sentiment"), Charset.defaultCharset());
        final BufferedWriter sentimentTestDataWriter = Files.newWriter(new File("/Users/rags/yelp_test_sentiment"), Charset.defaultCharset());
        final BufferedWriter ratingWriter = Files.newWriter(new File("/Users/rags/yelp_model_rating"), Charset.defaultCharset());
        final BufferedWriter ratingTestDataWriter = Files.newWriter(new File("/Users/rags/yelp_test_rating"), Charset.defaultCharset());
        int count = 0;
        for (ArrayList<Map<String, String>> restaurant : json("/Users/rags/Downloads/yelp_reviews_3.txt").values()) {
            for (Map<String, String> review : restaurant) {
                count++;
                BufferedWriter sentiment = (count > INT) ? sentimentTestDataWriter : sentimentWriter;
                BufferedWriter rating = (count > INT) ? ratingTestDataWriter : ratingWriter;

                final String review_text = review.get("text").replaceAll("\\.[\r\n]", " ").replaceAll("[\r\n]", " ").toLowerCase();
                sentiment.append(review.get("sentiment")).append(" ")
                        .append(review_text);
                sentiment.newLine();

                rating.append(valueOf(review.get("rating"))).append(" ")
                        .append(review_text);
                rating.newLine();
            }
        }
        sentimentWriter.close();
        sentimentTestDataWriter.close();
        ratingWriter.close();
        ratingTestDataWriter.close();
        /*System.out.println(Files.readLines(new File("/Users/rags/yelp_model_sentiment"), Charset.defaultCharset()));
        System.out.println(Files.readLines(new File("/Users/rags/yelp_model_rating"), Charset.defaultCharset()));*/
    }

}
