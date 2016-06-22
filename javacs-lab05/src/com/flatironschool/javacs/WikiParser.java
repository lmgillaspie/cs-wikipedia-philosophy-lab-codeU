/* This is a helper class to simplify the main WikiPhilosophy.java class 
 * Author: Lindsey Gillaspie
 */

package com.flatironschool.javacs;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.StringTokenizer;

public class WikiParser{

	private Elements list_paragraphs;

	// Keeps track of the open delimiters
	private Deque<String> parenthesisStack;

	// Initialize WikiParser
	public WikiParser(Elements list_paragraphs) {
		this.list_paragraphs = list_paragraphs;
		this.parenthesisStack = new ArrayDeque<String>();
	}

	// Try to find the first valid link
	public Element findFirstLink() {
		for (Element paragraph: list_paragraphs) {
			Element firstLink = findFirstLinkPara(paragraph);
			// As long as the link is not null, return it
			if(firstLink != null)
			{
				return firstLink;
			}
			// If the stack of delimeters is not empty, this means
			// the parentheses are unbalanced.
			if(!parenthesisStack.isEmpty()) 
			{
				System.err.println("Unbalance parentheses. BEWARE.");
			}
		}
		return null;	// Just in case
	}

	// If there is a valid link in a paragraph, this will return the first
	// link, else null. Used in findFirstLink().
	private Element findFirstLinkPara(Node root) {
		Iterable<Node> testNode = new WikiNodeIterable(root);

		for (Node node: testNode)	{
			// If a TextNode, get the parentheses
			if(node instanceof TextNode) 
			{
				processTextNode((TextNode) node);
			}
			
			// If an Element, find the links
			if(node instanceof Element)
			{
				Element firstLink = processElement((Element) node);
				// As long as the link is not null
				if(firstLink != null)
				{
					return firstLink;
				}
			}
		}

		return null; // Just in case
	}  // end findFirstLinkPara()


	// Split up a TextNode and check its parentheses
	private void processTextNode(TextNode node) {

		StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			
			// If a beginning parenthese is found, push on stack
			if (token.equals("(")) 
			{
				parenthesisStack.push(token);
			}
			// If an ending parenthese is found, pop a beginning
			// parenthese from the stack
			if (token.equals(")")) 
			{
				// Give an unbalanced warning in case the stacks empty
			        if (parenthesisStack.isEmpty()) 
				{
			        System.err.println("Unbalanced parentheses. BEWARE"); 
				}
			        parenthesisStack.pop();
			}
		}

	} // end processTextNode()


	// If a link is valid, return the element; null if not
	private Element processElement(Element elt) {
		// Basically just calls the validLink function
		if (validLink(elt)) 
		{
			return elt;
		}
		return null;
	}

	// Tests to see if a link is valid
	private boolean validLink(Element el) {
		// it's no good if it's
		if (!el.tagName().equals("a")) 
		{
			return false;
		}

		// Italics = not valid
		if (isItalic(el)) 
		{
			return false;
		}

		// In parenthesis = not valid
		if (!parenthesisStack.isEmpty()) 
		{
			return false;
		}

		// Bookmark = not valid
		if (el.attr("href").startsWith("#")) 
		{
			return false;
		}

		// Wiki help page = not valid
		if (el.attr("href").startsWith("/wiki/Help:")) 
		{
			return false;
		}
		return true;
	} // end of validLink()

	// Determine if an Element is in italics by following the parent links
	// up the tree. If there is an i or em tag in the parent chain, the link
	// is italics.
	private boolean isItalic(Element start) {
		
		for (Element el = start; el != null; el = el.parent()) 
		{
			if (el.tagName().equals("i") || el.tagName().equals("em")) 
			{
				return true;
			}
		}
		return false;
	}



}	// end class
