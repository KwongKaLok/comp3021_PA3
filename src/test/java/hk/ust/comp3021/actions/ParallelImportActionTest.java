package hk.ust.comp3021.actions;

import hk.ust.comp3021.action.ParallelImportAction;
import hk.ust.comp3021.action.UploadPaperAction;
import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.MiniMendeleyEngine;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParallelImportActionTest {

	@Tag(TestKind.PUBLIC)
	@Test
	void testPrallelImportAction_ActionsSize() {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		int originalSize = engine.getActions().size();
		ArrayList<String> bibFilePath = new ArrayList<>();
		bibFilePath.add("resources/Android.bib");
		bibFilePath.add("resources/ConstraintLogic.bib");
		bibFilePath.add("resources/neural.bib");
		bibFilePath.add("resources/FaultTolerant.bib");

		ParallelImportAction importAction = new ParallelImportAction("Action_1", user, new Date());
		importAction.setFilePaths(bibFilePath);
		engine.processParallelImport(user, importAction);

		int currentSize = engine.getActions().size();
	
		assertTrue(currentSize>= originalSize+1);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testPrallelImportAction_IsCompleted() {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> bibFilePath = new ArrayList<>();
		bibFilePath.add("resources/Android.bib");
		bibFilePath.add("resources/PAUploadData1.bib");

		ParallelImportAction importAction = new ParallelImportAction("Action_1", user, new Date());
		importAction.setFilePaths(bibFilePath);
		engine.processParallelImport(user, importAction);

		assertTrue(importAction.isCompleted());
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testPrallelImportAction_CheckPaperSet() {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> bibFilePath = new ArrayList<>();
		bibFilePath.add("resources/PAData.bib");
		bibFilePath.add("resources/PAUploadData1.bib");

		ParallelImportAction importAction = new ParallelImportAction("Action_1", user, new Date());
		importAction.setFilePaths(bibFilePath);
		engine.processParallelImport(user, importAction);

		Set<String> importedPaperIds =  importAction.getImportedPapers().keySet();
		System.out.print(importedPaperIds.size());
		assertTrue(importedPaperIds.size() > 0);
	}
	

	@Tag(TestKind.PUBLIC)
	@Test
	void testPrallelImportAction_CheckNumberOfFilePaths() {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());

		ArrayList<String> bibFilePath = new ArrayList<>();
		bibFilePath.add("resources/Android.bib");
		bibFilePath.add("resources/ConstraintLogic.bib");
		bibFilePath.add("resources/PAData.bib");
		bibFilePath.add("resources/FaultTolerant.bib");
		bibFilePath.add("resources/PCA.bib");
		bibFilePath.add("resources/FaultToleant.bib");
		bibFilePath.add("resources/neural.bib");
		bibFilePath.add("resources/InformationFlow.bib");
		bibFilePath.add("resources/Android.bib");
		bibFilePath.add("resources/ConstraintLogic.bib");
		bibFilePath.add("resources/neural.bib");
		bibFilePath.add("resources/FaultTolerant.bib");

		ParallelImportAction importAction = new ParallelImportAction("Action_1", user, new Date());
		importAction.setFilePaths(bibFilePath);
		engine.processParallelImport(user, importAction);
		
		assertFalse(importAction.isCompleted());
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void testPrallelImportAction_CheckPaperContent() {
		MiniMendeleyEngine engine = new MiniMendeleyEngine();
		String userID = "User_" + engine.getUsers().size();
		User user = engine.processUserRegister(userID, "testUser", new Date());
		ArrayList<String> bibFilePath = new ArrayList<>();
		bibFilePath.add("resources/Android.bib");
		bibFilePath.add("resources/PAUploadData1.bib");

		ParallelImportAction importAction = new ParallelImportAction("Action_1", user, new Date());
		importAction.setFilePaths(bibFilePath);
		HashMap<String, Paper> papers = importAction.getImportedPapers();

		String journalStr = "Proceedings of the ACM SIGPLAN Conference on Programming Language Design and Implementation (PLDI)";
		assertEquals(papers.get("Chase1990").getJournal(), journalStr);
		assertNotEquals(papers.get("Hutchison1973").getJournal(), journalStr);
		assertNotEquals(papers.get("Jones1979").getJournal(), journalStr);

		assertEquals(papers.get("Chase1990").getTitle(), "Analysis of pointers and structures");
		assertEquals(papers.get("Hutchison1973").getTitle(), "Lecture Notes in Computer Science");
		assertEquals(papers.get("Jones1979").getTitle(), "Flow analysis and optimization of LISP-like structures");
	}
}
