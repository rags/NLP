import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.featuregen.StringPattern;

import java.io.*;
import java.util.*;

import static java.lang.String.format;

/**
 * Created with IntelliJ IDEA.
 * User: aadithya
 * Date: 24/01/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeaturePresenceGenerator  implements FeatureGenerator {
    final POSTaggerME posTaggerME = posTagger();
    String pos;
    String neg;
    String unk;
    String[] words;
    List<String> w;
    String word_file;

    public FeaturePresenceGenerator() throws IOException {
//        pos = readFileAsString("pos");
//        neg = readFileAsString("neg");
//        unk = readFileAsString("unk");
        word_file = readFileAsString("senti_wn_top_words.txt");
        final Tokenizer tokenizer = tokenizer();
//        words = tokenizer.tokenize(word_file);
//        w =  Arrays.asList(words);
    }

    private TokenizerME tokenizer() throws IOException {
        return new TokenizerME(new TokenizerModel(getClass().getResourceAsStream("en-token.bin")));
    }

    @Override
    public Collection<String> extractFeatures(String[] text) {
        Collection<String> bagOfWords = new ArrayList<String>(text.length);
        List<String> text_list = Arrays.asList(text);
//        HashSet<String> hashSet = new HashSet();
//        final String[] tags = posTaggerME.tag(text);
//        for (String word:words) {
//            if (text_list.contains(word)){
//                bagOfWords.add("T");
//            }
//            else{
//                bagOfWords.add("F");
//            }
//        }

        for (int i = 0; i < text.length; i++) {
//            if (tags[i].startsWith("JJ") || tags[i].startsWith("NN") || tags[i].startsWith("R")) {
              if(check_exists(text[i])){
                  bagOfWords.add(text[i]);
//              }
            }
        }
        return bagOfWords;
    }

    private boolean check_exists(String word) {
//        if(pos.contains(word)){
//            return true;
//        }
//        else if(neg.contains(word)){
//            return true;
//        }
//        else if(unk.contains(word)){
//            return true;
//        }
        if (word_file.contains(word)){
            return true;
        }
        return false;
    }

    private POSTaggerME posTagger() {
        try {
            return new POSTaggerME(new POSModel(getClass().getResourceAsStream("en-pos-maxent.bin")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
