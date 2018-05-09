package edu.brown.cs.sgd.processing;

import edu.brown.cs.sgd.general.General;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/***
 * Handle interfacing with the Name Entity Recognition API.**
 * 
 * @author gk16
 */
public class NERProcessor {

  private final String serializedClassifier;
  private AbstractSequenceClassifier<CoreLabel> classifier;

  public NERProcessor(String serializedClassifier) {
    if (serializedClassifier == null) {
      this.serializedClassifier =
          "dependencies/english.all.3class.distsim.crf.ser.gz";
    } else {
      this.serializedClassifier = serializedClassifier;
    }
    try {
      classifier = CRFClassifier.getClassifier(this.serializedClassifier);
    } catch (Exception e) {
      General.printErr(e.getMessage());
    }
  }

  public String process(String input) {
    return classifier.classifyToString(input, "tabbedEntities", false);
  }

}
