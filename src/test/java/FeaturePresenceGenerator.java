import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.featuregen.StringPattern;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
    String words;

    public FeaturePresenceGenerator() throws IOException {
//        pos = readFileAsString("pos");
//        neg = readFileAsString("neg");
//        unk = readFileAsString("unk");
        words = readFileAsString("temp_pdndud252525.txt");

    }

    @Override
    public Collection<String> extractFeatures(String[] text) {
        Collection<String> bagOfWords = new ArrayList<String>(text.length);
        HashSet<String> hashSet = new HashSet();
        final String[] tags = posTaggerME.tag(text);
        for (int i = 0; i < text.length; i++) {
//            if (tags[i].startsWith("JJ") || tags[i].startsWith("NN") || tags[i].startsWith("R")) {
              if(check_exists(text[i])){
                  hashSet.add(text[i]);
//              }
            }
        }
        return hashSet;
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
        if (words.contains(word)){
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
