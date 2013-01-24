import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.util.featuregen.StringPattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: aadithya
 * Date: 24/01/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeaturePresenceGenerator  implements FeatureGenerator {
    @Override
    public Collection<String> extractFeatures(String[] text) {
        Collection<String> bagOfWords = new ArrayList<String>(text.length);
        HashSet<String> hashSet = new HashSet();

        for (int i = 0; i < text.length; i++) {
            hashSet.add(text[i]);
        }
        return hashSet;
    }
}
