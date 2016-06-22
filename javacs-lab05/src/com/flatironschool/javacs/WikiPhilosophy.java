/* Program edits made by Lindsey Gillaspie */

package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	final static List<String> visited_URLs = new ArrayList<String>();	

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
         * 2. Ignoring external links, links to the current page, or red links
         * 3. Stopping when reaching "Philosophy", a page with no links or a page
         *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		String start_url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String final_URL = "https://en.wikipedia.org/wiki/Philosophy";
		find_URL(start_url, final_URL);
	}

	// The starting URL is followed until the destination (or none) is found
	public static void find_URL(String starting, String ending) throws IOException{
	
		String start_copy = starting;
		for (int i = 0; i < 10; i++) {
			if(visited_URLs.contains(start_copy)) {
				System.err.println("Dueces. (stuck in loop)");
				return;
			} 
			else 
			{
				visited_URLs.add(start_copy);
			}
			Element emt = getFirstValidLink(start_copy);
			if (emt == null) {
				System.err.println("Dueces. (no valid links)");
				return;
			}
			
			start_copy = emt.attr("abs:href");
			if (start_copy.equals(ending)) {
				System.out.println("We did it!");
				break;
			}
		}

	}

	// Load and parse the URL using WikiParser class, then extract the first link
	public static Element getFirstValidLink(String url) throws IOException {

		// 1. Take a URL for a Wikipedia page
		Elements paragraphs = wf.fetchWikipedia(url);
		WikiParser wp = new WikiParser(paragraphs);
		Element em = wp.findFirstLink();
		return em;
	}

       public static void print(String message, Object... args){
		System.out.println(String.format(message, args));
       }
}
