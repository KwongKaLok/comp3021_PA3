package hk.ust.comp3021.actions;

import hk.ust.comp3021.action.Action;
import hk.ust.comp3021.action.LabelAction;
import hk.ust.comp3021.action.LabelActionList;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.MiniMendeleyEngine;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class ModifyLabelListTest {
	LabelActionList actionList = new LabelActionList();
	private Thread[] threads = new Thread[3];
	private Runnable[] runnables = new Runnable[3];
	private MiniMendeleyEngine engine = new MiniMendeleyEngine();
	private User user;


	@Tag(TestKind.PUBLIC)
	@Test
	void ModifyLabelList_CheckDuplicateDelete() throws InterruptedException {
		LabelAction labelAction = new LabelAction("Action_1", user, new Date(), Action.ActionType.ADD_LABEL, "TEST1");
		labelAction.setPaperID(engine.getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction);

		LabelAction labelAction1 = new LabelAction("Action_2", user, new Date(), Action.ActionType.ADD_LABEL, "TEST2");
		labelAction1.setPaperID(engine.getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction1);

		LabelAction labelAction2 = new LabelAction("Action_3", user, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST2");
		actionList.enqueue(labelAction2);

		LabelAction labelAction3 = new LabelAction("Action_4", user, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST1");
		actionList.enqueue(labelAction3);

		LabelAction labelAction4 = new LabelAction("Action_5", user, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST1");
		actionList.enqueue(labelAction4);

		String userID = "User_" + engine.getUsers().size();
		user = engine.processUserRegister(userID, "testUser", new Date());

		// create the thread for adding new labels
		runnables[0] = engine.processAddLabel(user, actionList);
		threads[0] = new Thread(runnables[0]);
		threads[0].start();

		// create the thread for updating labels
		runnables[1] = engine.processUpdateLabel(user, actionList);
		threads[1] = new Thread(runnables[1]);
		threads[1].start();

		// create the thread for removing the labels
		runnables[2] = engine.processDeleteLabel(user, actionList);
		threads[2] = new Thread(runnables[2]);
		threads[2].start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		actionList.setFinished(true);
		assertTrue(actionList.getProcessedLabels().size() >= 1);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void ModifyLabelList_CheckDuplicateAdd() throws InterruptedException {
		LabelActionList actionList = new LabelActionList();

		LabelAction labelAction = new LabelAction("Action_1", user, new Date(), Action.ActionType.ADD_LABEL, "TEST1");
		labelAction.setPaperID(engine.getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction);

		LabelAction labelAction1 = new LabelAction("Action_2", user, new Date(), Action.ActionType.ADD_LABEL, "TEST1");
		labelAction1.setPaperID(engine.getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction1);

		LabelAction labelAction2 = new LabelAction("Action_3", user, new Date(), Action.ActionType.UPDATE_LABELS,
				"TEST2");
		labelAction2.setLabel("TEST1");
		actionList.enqueue(labelAction2);

		// create the thread for adding new labels
		runnables[0] = engine.processAddLabel(user, actionList);
		threads[0] = new Thread(runnables[0]);
		threads[0].start();

		// create the thread for updating labels
		runnables[1] = engine.processUpdateLabel(user, actionList);
		threads[1] = new Thread(runnables[1]);
		threads[1].start();

		// create the thread for removing the labels
		runnables[2] = engine.processDeleteLabel(user, actionList);
		threads[2] = new Thread(runnables[2]);
		threads[2].start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		actionList.setFinished(true);
		assertTrue(actionList.getProcessedLabels().size() >= 1);
	}

	@Tag(TestKind.PUBLIC)
	@Test
	void ModifyLabelList_CheckDuplicateUpdate() throws InterruptedException {
		LabelAction labelAction = new LabelAction("Action_1", user, new Date(), Action.ActionType.ADD_LABEL, "TEST1");
		labelAction.setPaperID(engine.getPaperBase().keySet().toArray()[0].toString());
		actionList.enqueue(labelAction);

		LabelAction labelAction1 = new LabelAction("Action_2", user, new Date(), Action.ActionType.ADD_LABEL, "TEST1");
		labelAction1.setPaperID(engine.getPaperBase().keySet().toArray()[1].toString());
		actionList.enqueue(labelAction1);

		LabelAction labelAction2 = new LabelAction("Action_3", user, new Date(), Action.ActionType.ADD_LABEL, "TEST1");
		labelAction2.setPaperID(engine.getPaperBase().keySet().toArray()[4].toString());
		actionList.enqueue(labelAction2);

		LabelAction labelAction3 = new LabelAction("Action_4", user, new Date(), Action.ActionType.ADD_LABEL, "TEST2");
		labelAction3.setPaperID(engine.getPaperBase().keySet().toArray()[6].toString());
		actionList.enqueue(labelAction3);

		LabelAction labelAction4 = new LabelAction("Action_5", user, new Date(), Action.ActionType.ADD_LABEL, "TEST3");
		labelAction4.setPaperID(engine.getPaperBase().keySet().toArray()[2].toString());
		actionList.enqueue(labelAction4);

		LabelAction labelAction5 = new LabelAction("Action_6", user, new Date(), Action.ActionType.ADD_LABEL, "TEST4");
		labelAction5.setPaperID(engine.getPaperBase().keySet().toArray()[1].toString());
		actionList.enqueue(labelAction5);

		LabelAction labelAction6 = new LabelAction("Action_7", user, new Date(), Action.ActionType.UPDATE_LABELS,
				"TEST3");
		labelAction6.setNewLabel("TEST2");
		actionList.enqueue(labelAction6);

		LabelAction labelAction7 = new LabelAction("Action_8", user, new Date(), Action.ActionType.UPDATE_LABELS,
				"TEST2");
		labelAction7.setNewLabel("TEST3");
		actionList.enqueue(labelAction7);

		LabelAction labelAction8 = new LabelAction("Action_9", user, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST3");
		actionList.enqueue(labelAction8);

		LabelAction labelAction9 = new LabelAction("Action_10", user, new Date(), Action.ActionType.DELETE_LABELS,
				"TEST2");
		actionList.enqueue(labelAction9);

		// create the thread for adding new labels
		runnables[0] = engine.processAddLabel(user, actionList);
		threads[0] = new Thread(runnables[0]);
		threads[0].start();

		// create the thread for updating labels
		runnables[1] = engine.processUpdateLabel(user, actionList);
		threads[1] = new Thread(runnables[1]);
		threads[1].start();

		// create the thread for removing the labels
		runnables[2] = engine.processDeleteLabel(user, actionList);
		threads[2] = new Thread(runnables[2]);
		threads[2].start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		actionList.setFinished(true);
		assertTrue(actionList.getProcessedLabels().size() >= 1);
	}
}
