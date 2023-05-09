package hk.ust.comp3021.actions;

import hk.ust.comp3021.action.Action;
import hk.ust.comp3021.action.QueryAction;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.Query;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.MiniMendeleyEngine;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentQueryTest {

	@Tag(TestKind.PUBLIC)
	@Test
	void QueryProcessing_ActionSize() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		int original = engine.getActions().size();
		QueryAction action = new QueryAction("Action_1", user, new Date(), Action.ActionType.PROCESS_QUERY);
		action.setFilePath("resources/query.txt");
		engine.processConcurrentQuery(user, action);

		int current = engine.getActions().size();

		assertEquals(original + 1, current);
	}
	
	@Tag(TestKind.PUBLIC)
	@Test
	void QueryProcessing_CheckResult() throws InterruptedException {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		QueryAction action = new QueryAction("Action_1", user, new Date(), Action.ActionType.PROCESS_QUERY);
		action.setFilePath("resources/query.txt");
		engine.processConcurrentQuery(user, action);

		int completed = 0;
		for (Query query : action.getQueries())
			if (query.isCompleted())
				completed++;

		assertEquals(completed, 18);
	}
}
