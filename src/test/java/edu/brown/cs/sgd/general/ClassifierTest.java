package edu.brown.cs.sgd.general;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import edu.brown.cs.sgd.classifier.SimpleClassifier;

public class ClassifierTest {

	@Test
	public void testClassifier() {
		SimpleClassifier sc = new SimpleClassifier();
		String text = "This is a test text string to analyze!";
		Map<String, Double> result = sc.analyzeText(text);
		System.out.println(result);
		assert(!result.isEmpty());
	}
	
	@Test
	public void testCaseFixing() {
		SimpleClassifier sc = new SimpleClassifier();
		String text = "I'm a happy person. Detectives are murder! trump";
		Map<String, Double> result = sc.analyzeText(text);
		System.out.println(result);
		assert(result.get("love").doubleValue() == 0.0);
		assert(result.get("political").doubleValue() == 12.5);
	}
	
	@Test
	public void testCasePercentages() {
		SimpleClassifier sc = new SimpleClassifier();
		String text = "WASHINGTON — President Trump on Thursday directly contradicted his earlier statements that he knew of no payment to Stormy Daniels, the pornographic film actress who says she had an affair with him.\n" + 
				"\n" + 
				"Mr. Trump said he paid a monthly retainer to his former lawyer and fixer, Michael D. Cohen, and suggested that the payment by Mr. Cohen to the actress could not be considered a campaign contribution.\n" + 
				"\n" + 
				"The president’s comments reiterated an explosive announcement late Wednesday by one of his recently-hired attorneys, Rudolph W. Giuliani, who said on Fox News that the president reimbursed Mr. Cohen for the payment to the actress Stephanie Clifford, who performs as Stormy Daniels. Though Mr. Giuliani described his interview as part of a strategy, the disclosure caught several Trump advisers by surprise, sending some scrambling on Thursday morning to determine how to confront the situation.\n" + 
				"\n" + 
				"In three Twitter posts Thursday morning, the president repeated some of what Mr. Giuliani said a day earlier, specifically that Mr. Trump repaid a $130,000 payment Mr. Cohen made to Ms. Clifford just days before the presidential election in 2016.\n" + 
				"\n" + 
				"Mr. Giuliani and Mr. Trump said this removed the question of whether it was a campaign finance violation. Mr. Trump also continued to deny the affair.";
		Map<String, Double> result = sc.analyzeText(text);
		System.out.println(result);
	}

}
