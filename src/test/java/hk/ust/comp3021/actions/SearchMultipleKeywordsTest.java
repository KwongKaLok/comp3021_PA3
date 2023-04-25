package hk.ust.comp3021.actions;

import hk.ust.comp3021.action.SearchMultipleKeywordsAction;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.MiniMendeleyEngine;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class SearchMultipleKeywordsTest {

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_ActionsSize() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		int originalSize = engine.getActions().size();
		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		int currentSize = engine.getActions().size();
		assertEquals(currentSize, originalSize + 1);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_IsFound() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertTrue(searchMultipleKeywordsAction.isFound());
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_CheckNumberOfWords() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertFalse(searchMultipleKeywordsAction.getWords().size() > 10);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_CheckNumberOfWords1() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");
		words.add("graph");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertTrue(searchMultipleKeywordsAction.getWords().size() < 10);
		assertTrue(searchMultipleKeywordsAction.isFound());
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testSearchMultipleKeywords_CheckNumberOfWords2() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> words = new ArrayList<String>();
		words.add("graph");
		words.add("component");
		words.add("constraint");
		words.add("security");
		words.add("blockchain");
		words.add("components");
		words.add("compiler");
		words.add("static");
		words.add("fuzzing");
		words.add("dynamic");
		words.add("graph");
		words.add("book");
		words.add("cat");
		words.add("kebab");
		words.add("paper");
		words.add("journal");
		words.add("sand");
		words.add("snake");
		words.add("bab");
		words.add("par");
		words.add("jourl");
		words.add("and");
		words.add("tom");

		SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction("Action_1", user,
				new Date());
		searchMultipleKeywordsAction.setWords(words);
		engine.processMultiKeywordSearch(user, searchMultipleKeywordsAction);

		assertTrue(searchMultipleKeywordsAction.getWords().size() > 20);
	}
}
