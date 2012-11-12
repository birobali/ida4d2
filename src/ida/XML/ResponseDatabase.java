package ida.XML;

import ida.responses.Response;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResponseDatabase {
	Document doc;
	NodeList keywordNodes;

	/*
	 * Store keyword nodes for searching purposes. Node.getParent() should be
	 * useful in this case.
	 */
	public ResponseDatabase() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.parse("responses.xml");

		keywordNodes = doc.getElementsByTagName("Keyword");
	}

	/**
	 * Find keywords similar Group with responses Compare weights for the
	 * response pick best response.
	 */
	public Response getResponse(LinkedList<String> keywords) {

		// Get the similar keyword nodes
		LinkedList<Node> similarKeywords = new LinkedList<Node>();
		for (int i = 0; i < keywordNodes.getLength(); i++) {
			if (keywords.contains(keywordNodes.item(i).getFirstChild().getNodeValue())) {
				similarKeywords.add(keywordNodes.item(i));
			}
		}
		
		if (similarKeywords.size()==0)
		{
			for (int i=0; i<keywordNodes.getLength();i++)
			{
				if (keywordNodes.item(i).getFirstChild().getNodeValue().equals("NOKEYFOUND"))
				{
					similarKeywords.add(keywordNodes.item(keywordNodes.getLength()-1));
				}
			}
			
		}

		// Get all the health functions for the keywords
		double currentHealth = 0.0;
		Node bestResponse = null;
		double best = -1;

		for (int i = 0; i < similarKeywords.size(); i++) {
			if (i != 0) {
				if (similarKeywords.get(i).getParentNode() != similarKeywords.get(i - 1).getParentNode()) {
					if (currentHealth >= best) {
						bestResponse = similarKeywords.get(i - 1).getParentNode().getParentNode();
						best = currentHealth;
					}
					currentHealth = 0;
				}
			}
			String weight = similarKeywords.get(i).getAttributes().getNamedItem("weight").getNodeValue();
			currentHealth += Double.parseDouble(weight);
		}

		if (currentHealth >= best) {
			bestResponse = similarKeywords.get(similarKeywords.size() - 1).getParentNode().getParentNode();
			best = currentHealth;
		}

		// TODO Get a random response
		if (bestResponse != null) {
			Node messagesNode = bestResponse.getChildNodes().item(3);
			int messagesNodeLength = messagesNode.getChildNodes().getLength();
			LinkedList<String> messages = new LinkedList<String>();
			for (int i=0; i < messagesNodeLength; i++){
				String content = messagesNode.getChildNodes().item(i).getTextContent();
				if (i%2!=0){
					messages.add(content);
				}
			}
			Random random = new Random();
			return new Response(messages.get(random.nextInt(messages.size())));
		}

		return new Response("A failure occured in response retrieval.");
	}

}
