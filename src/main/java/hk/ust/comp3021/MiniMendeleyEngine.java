package hk.ust.comp3021;

import java.io.BufferedReader;
//import java.io.File;
import java.io.FileReader;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import hk.ust.comp3021.action.Action;
import hk.ust.comp3021.action.AddCommentAction;
import hk.ust.comp3021.action.AddLabelAction;
import hk.ust.comp3021.action.DownloadPaperAction;
import hk.ust.comp3021.action.LabelAction;
import hk.ust.comp3021.action.LabelActionList;
import hk.ust.comp3021.action.ParallelImportAction;
import hk.ust.comp3021.action.QueryAction;
import hk.ust.comp3021.action.SearchMultipleKeywordsAction;
import hk.ust.comp3021.action.SearchPaperAction;
import hk.ust.comp3021.action.SearchPaperAction.SearchPaperKind;
import hk.ust.comp3021.action.SearchResearcherAction;
import hk.ust.comp3021.action.SearchResearcherAction.SearchResearcherKind;
import hk.ust.comp3021.action.SortPaperAction;
import hk.ust.comp3021.action.SortPaperAction.SortBase;
import hk.ust.comp3021.action.SortPaperAction.SortKind;
import hk.ust.comp3021.action.StatisticalInformationAction;
import hk.ust.comp3021.action.StatisticalInformationAction.InfoKind;
import hk.ust.comp3021.action.UploadPaperAction;
import hk.ust.comp3021.person.Researcher;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Comment;
import hk.ust.comp3021.resource.Comment.CommentType;
import hk.ust.comp3021.resource.Label;
import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.utils.BibExporter;
import hk.ust.comp3021.utils.BibParser;
import hk.ust.comp3021.utils.Query;
import hk.ust.comp3021.utils.UserRegister;

public class MiniMendeleyEngine {
  private final String defaultBibFilePath = "resources/bibdata/PAData.bib";
  private final HashMap<String, Paper> paperBase = new HashMap<>();
  private final ArrayList<User> users = new ArrayList<>();
  private final ArrayList<Researcher> researchers = new ArrayList<>();

  private final ArrayList<Comment> comments = new ArrayList<>();

  private final ArrayList<Label> labels = new ArrayList<>();

  private final ArrayList<Action> actions = new ArrayList<>();

  private Queue<LabelAction> labelActionsQueue = new LinkedList<>();

  public MiniMendeleyEngine() {
    populatePaperBaseWithDefaultBibFile();
  }

  public void populatePaperBaseWithDefaultBibFile() {
    User user = new User("User_0", "root_user", new Date());
    users.add(user);
    UploadPaperAction action = new UploadPaperAction("Action_0", user, new Date(), defaultBibFilePath);
    processUploadPaperAction(user, action);
    paperBase.putAll(action.getUploadedPapers());
  }

  public String getDefaultBibFilePath() {
    return defaultBibFilePath;
  }

  public HashMap<String, Paper> getPaperBase() {
    return paperBase;
  }

  public ArrayList<User> getUsers() {
    return users;
  }

  public ArrayList<Researcher> getResearchers() {
    return researchers;
  }

  public ArrayList<Comment> getComments() {
    return comments;
  }

  public ArrayList<Label> getLabels() {
    return labels;
  }

  public ArrayList<Action> getActions() {
    return actions;
  }

  public User processUserRegister(String id, String name, Date date) {
    UserRegister ur = new UserRegister(id, name, date);
    User curUser = ur.register();
    users.add(curUser);
    return curUser;
  }

  public Comment processAddCommentAction(User curUser, AddCommentAction action) {
    actions.add(action);
    if (action.getCommentType() == CommentType.COMMENT_OF_COMMENT) {
      String objCommentID = action.getObjectId();
      for (Comment comment : comments) {
        if (objCommentID.equals(comment.getCommentID())) {
          String commentID = "Comment" + String.valueOf(comments.size() + 1);
          Comment newComment = new Comment(commentID, action.getTime(), action.getCommentStr(), action.getUser(),
              action.getCommentType(), action.getObjectId());
          comments.add(newComment);
          comment.appendComment(newComment);
          curUser.appendNewComment(newComment);
          action.setActionResult(true);
          return newComment;
        }
      }
    } else if (action.getCommentType() == CommentType.COMMENT_OF_PAPER) {
      String objCommentID = action.getObjectId();
      for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
        String paperID = entry.getKey();
        if (paperID.equals(objCommentID)) {
          String commentID = "Comment" + String.valueOf(comments.size() + 1);
          Comment newComment = new Comment(commentID, action.getTime(), action.getCommentStr(), action.getUser(),
              action.getCommentType(), action.getObjectId());
          comments.add(newComment);
          entry.getValue().appendComment(newComment);
          curUser.appendNewComment(newComment);
          action.setActionResult(true);
          return newComment;
        }
      }
    }
    action.setActionResult(false);
    return null;
  }

  public Label processAddLabelAction(User curUser, AddLabelAction action) {
    actions.add(action);
    String paperID = action.getPaperID();
    String labelID = "Label" + String.valueOf(labels.size() + 1);
    Label newLabel = new Label(labelID, action.getPaperID(), action.getTime(), action.getLabelStr(), action.getUser());

    if (paperBase.containsKey(paperID)) {
      paperBase.get(paperID).appendLabelContent(newLabel);
      curUser.appendNewLabel(newLabel);
      labels.add(newLabel);
      action.setActionResult(true);
      return newLabel;
    } else {
      action.setActionResult(false);
      return null;
    }
  }

  public void processDownloadPaperAction(User curUser, DownloadPaperAction action) {
    actions.add(action);
    String path = action.getDownloadPath();
    String content = "";
    HashMap<String, Paper> downloadedPapers = new HashMap<>();
    for (String paperID : action.getPaper()) {
      if (paperBase.containsKey(paperID)) {
        downloadedPapers.put(paperID, paperBase.get(paperID));
      } else {
        action.setActionResult(false);
        return;
      }
    }
    BibExporter exporter = new BibExporter(downloadedPapers, path);
    exporter.export();
    action.setActionResult(!exporter.isErr());
  }

  public ArrayList<Paper> processSearchPaperAction(User curUser, SearchPaperAction action) {
    actions.add(action);
    switch (action.getKind()) {
    case ID:
      for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
        if (action.getSearchContent().equals(entry.getKey())) {
          action.appendToActionResult(entry.getValue());
        }
      }
      break;
    case TITLE:
      for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
        if (action.getSearchContent().equals(entry.getValue().getTitle())) {
          action.appendToActionResult(entry.getValue());
        }
      }
      break;
    case AUTHOR:
      for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
        if (entry.getValue().getAuthors().contains(action.getSearchContent())) {
          action.appendToActionResult(entry.getValue());
        }
      }
      break;
    case JOURNAL:
      for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
        if (action.getSearchContent().equals(entry.getValue().getJournal())) {
          action.appendToActionResult(entry.getValue());
        }
      }
      break;
    default:
      break;
    }
    return action.getActionResult();
  }

  public ArrayList<Paper> processSearchPaperActionByLambda(User curUser, SearchPaperAction action) {
    actions.add(action);
    switch (action.getKind()) {
    case ID:
      paperBase.entrySet().forEach(entry -> {
        if (action.isEqual.test(entry.getKey()))
          action.appendToActionResultByLambda.accept(entry.getValue());
      });
      break;
    case TITLE:
      paperBase.entrySet().forEach(entry -> {
        if (action.isEqual.test(entry.getValue().getTitle()))
          action.appendToActionResultByLambda.accept(entry.getValue());
      });
      break;
    case AUTHOR:
      paperBase.entrySet().forEach(entry -> {
        if (action.isContain.test(entry.getValue().getAuthors()))
          action.appendToActionResultByLambda.accept(entry.getValue());
      });
      break;
    case JOURNAL:
      paperBase.entrySet().forEach(entry -> {
        if (action.isEqual.test(entry.getValue().getJournal()))
          action.appendToActionResultByLambda.accept(entry.getValue());
      });
      break;
    default:
      break;
    }
    return action.getActionResult();
  }

  public List<Paper> processSortPaperActionByLambda(User curUser, SortPaperAction action) {
    actions.add(action);
    paperBase.entrySet().forEach(entry -> {
      action.appendToActionResultByLambda.accept(entry.getValue());
    });
    switch (action.getBase()) {
    case ID:
      action.comparator = (paper1, paper2) -> stringProcessNullSafe(paper1.getPaperID(), paper2.getPaperID());
      if (action.kindPredicate.test(action.getKind()))
        action.comparator = action.comparator.reversed();
      break;
    case TITLE:
      action.comparator = (paper1, paper2) -> stringProcessNullSafe(paper1.getTitle(), paper2.getTitle());
      if (action.kindPredicate.test(action.getKind()))
        action.comparator = action.comparator.reversed();
      break;
    case AUTHOR:
      action.comparator = (paper1, paper2) -> {
        return stringProcessNullSafe(String.join(",", paper1.getAuthors()), String.join(",", paper2.getAuthors()));
      };
      if (action.kindPredicate.test(action.getKind()))
        action.comparator = action.comparator.reversed();
      break;
    case JOURNAL:
      action.comparator = (paper1, paper2) -> stringProcessNullSafe(paper1.getJournal(), paper2.getJournal());
      if (action.kindPredicate.test(action.getKind()))
        action.comparator = action.comparator.reversed();
      break;
    default:
      break;
    }
    action.sortFunc.get();
    return action.getActionResult();
  }

  public HashMap<String, List<Paper>> processSearchResearcherActionByLambda(User curUser,
      SearchResearcherAction action) {
    actions.add(action);
    paperBase.entrySet().forEach(entry -> {
      entry.getValue().getAuthors().forEach(author -> action.appendToActionResult(author, entry.getValue()));
    });
    switch (action.getKind()) {
    case PAPER_WITHIN_YEAR:
      action.searchFunc1.get();
      break;
    case JOURNAL_PUBLISH_TIMES:
      action.searchFunc2.get();
      break;
    case KEYWORD_SIMILARITY:
      action.searchFunc3.get();
      break;

    default:
      break;
    }
    return action.getActionResult();
  }

  int stringProcessNullSafe(String str1, String str2) {
    if (str1 == null && str2 == null)
      return 0;
    if (str1 == null)
      return -1;
    if (str2 == null)
      return 1;
    return str1.compareTo(str2);
  }

  public Map<String, Double> processStatisticalInformationActionByLambda(User curUser,
      StatisticalInformationAction action) {
    actions.add(action);
    List<Paper> paperList = new ArrayList<Paper>();
    paperBase.entrySet().forEach(entry -> paperList.add(entry.getValue()));
    switch (action.getKind()) {
    case AVERAGE:
      action.obtainer1.apply(paperList);
      break;
    case MAXIMAL:
      action.obtainer2.apply(paperList);
      break;
    default:
      break;
    }
    return action.getActionResult();
  }

  public void processUploadPaperAction(User curUser, UploadPaperAction action) {
    actions.add(action);
    BibParser parser = new BibParser(action.getBibfilePath());
    parser.parse();
    action.setUploadedPapers(parser.getResult());
    for (String paperID : action.getUploadedPapers().keySet()) {
      Paper paper = action.getUploadedPapers().get(paperID);
      paperBase.put(paperID, paper);
      for (String researcherName : paper.getAuthors()) {
        Researcher existingResearch = null;
        for (Researcher researcher : researchers) {
          if (researcher.getName().equals(researcherName)) {
            existingResearch = researcher;
            break;
          }
        }
        if (existingResearch == null) {
          Researcher researcher = new Researcher("Researcher_" + researchers.size(), researcherName);
          researcher.appendNewPaper(paper);
          researchers.add(researcher);
        } else {
          existingResearch.appendNewPaper(paper);
        }
      }
    }
    action.setActionResult(!parser.isErr());
  }

  /**
   * TODO: Implement the new searching method with Lambda expressions using
   * functional interfaces. The thing you need to do is to implement the three
   * functional interfaces, i.e., searchFunc1 / searchFunc2 /searchFunc3. The
   * prototypes for the functional interfaces are in `SearchResearcherAction`. PS:
   * You should operate directly on `actionResult` since we have already put the
   * papers into it.
   */

  /**
   * TODO Implement code in this function to perform the importation of more than
   * one bib file in parallel
   * @param curUser
   * @param parallelImportAction: an action of parallel import that includes the
   *                              list of path files entered by the user
   */
  public void processParallelImport(User curUser, ParallelImportAction parallelImportAction) {
    List<String> tempFiles = new ArrayList<String>();
    Thread[] threads = new Thread[parallelImportAction.maxNumberofThreads()];
    for (String filePath : parallelImportAction.getFilePaths()) {
      tempFiles.add(filePath);
    }

    for (int i = 0; i < parallelImportAction.maxNumberofThreads(); i++) {
      threads[i] = new Thread(() -> {
        synchronized (this) {
          while (!tempFiles.isEmpty()) {
            String tempFilePath = tempFiles.remove(0);
            this.notifyAll();
            try {
              BibParser parser = new BibParser(tempFilePath);
              parser.parse();
              parallelImportAction.setImportedPapers(parser.getResult());
              for (Map.Entry<String, Paper> entry : parser.getResult().entrySet()) {
                paperBase.put(entry.getKey(), entry.getValue());
              }
            } catch (Exception e) {
            }
            try {
              if (!tempFiles.isEmpty()) {
                this.wait();
              }
            } catch (InterruptedException e) {
            }
          }
        }
      });
      threads[i].start();
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
      }
    }
    if (parallelImportAction.getFilePaths().size() > parallelImportAction.maxNumberofThreads()) {
      parallelImportAction.setCompleted(false);
    } else {
      parallelImportAction.setCompleted(true);
    }
    this.getActions().add(parallelImportAction);
  }

  /**
   * TODO Implement this function using only 5 threads to search papers for
   * the @words in @paperBase and store the results in @results variable
   * in @multipleSearch At the end, print the title and paperId of the papers that
   * matches the search and print the number of results. Store the number of found
   * results in @foundResults in @multipleSearch
   * @param curUser
   * @param multipleSearch: the list of words to search for
   * @throws InterruptedException
   */
  public void processMultiKeywordSearch(User curUser, SearchMultipleKeywordsAction multipleSearch)
      throws InterruptedException {
    ArrayList<String> tempWords = new ArrayList<>();
    for (String word : multipleSearch.getWords()) {
      if (!tempWords.contains(word)) {
        tempWords.add(word);
      }
    }
    multipleSearch.setWords(tempWords);

    Thread[] searchThreads = new Thread[5];
    Semaphore semaphore = new Semaphore(5);
    ArrayList<Paper> results = new ArrayList<>();

    for (int i = 0; i < multipleSearch.getWords().size(); i++) {
      String word = multipleSearch.getWords().get(i);
      int threadIndex = i % 5;
      searchThreads[threadIndex] = new Thread(() -> {
        try {
          semaphore.acquire();
          for (String paperId : paperBase.keySet()) {
            Paper paper = paperBase.get(paperId);
            if (paper.getTitle() != null && paper.getTitle().contains(word)) {
              synchronized (this) {
                results.add(paper);
                multipleSearch.increaseFound();
                multipleSearch.setFound(true);
              }
            } else if (paper.getKeywords() != null && paper.getKeywords().contains(word)) {
              synchronized (this) {
                results.add(paper);
                multipleSearch.increaseFound();
                multipleSearch.setFound(true);
              }
            } else if (paper.getAbsContent() != null && paper.getAbsContent().contains(word)) {
              synchronized (this) {
                results.add(paper);
                multipleSearch.increaseFound();
                multipleSearch.setFound(true);
              }
            }
          }
          synchronized (this) {
            multipleSearch.setResults(results);
          }
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        } finally {
          semaphore.release();
        }
      });
    }

    for (Thread thread : searchThreads) {
      if (thread != null) {
        thread.start();
      }
    }

    for (Thread thread : searchThreads) {
      if (thread != null) {
        thread.join();
      }
    }

    this.getActions().add(multipleSearch);
  }

  /***
   * TODO Implement the code for this method to perform the creation of a new
   * label for a paper.
   * @param curUser
   * @param actionList: holds the list of actions to be performed one after
   *                    another
   */
  public Runnable processAddLabel(User curUser, LabelActionList actionList) {
    return new Runnable() {
      @Override
      public void run() {
//        synchronized (actionList) {
          try {
            LabelAction labelAction;
            while ((labelAction = actionList.getHead()) != null&&labelAction.getActionType().equals(Action.ActionType.ADD_LABEL)) {
//              if (labelAction.getActionType().equals(Action.ActionType.ADD_LABEL)) {
                AddLabelAction action = new AddLabelAction("Action_" + labelAction.getId(), curUser,
                    labelAction.getTime(), labelAction.getLabel(), labelAction.getPaperID());
                processAddLabelAction(curUser, action);
                actionList.dequeue();
//              }
//                actionList.notifyAll();
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
//        }
      }
    };
  }

  /***
   * TODO Implement the code for this method to perform the updating of a new
   * label for a paper
   * @param curUser
   * @param actionList: holds the list of actions to be performed one after
   *                    another
   */
  public Runnable processUpdateLabel(User curUser, LabelActionList actionList) {
    return new Runnable() {
      @Override
      public void run() {
              try{
                  while (!actionList.isFinished()){
                      LabelAction labelAction= actionList.getHead();
                      while (labelAction==null||!labelAction.getActionType().equals(Action.ActionType.UPDATE_LABELS) ){
                          labelAction = actionList.getHead();
                      }
                      String label = labelAction.getLabel();
                      if (!actionList.getProcessedLabels().contains(label)) {
                          actionList.addProcessedLabel(label);
                      }
                      for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
                          Paper paper = entry.getValue();
                          for (Label paperLabel : paper.getLabels()) {
                              if (paperLabel.getContent().equals(label)) {
                                  paperLabel.setContent(labelAction.getNewLabel());
                              }
                          }
                      }
                      for (Label tempLabel : labels) {
                          if (tempLabel.getContent().equals(label)) {
                              tempLabel.setContent(labelAction.getNewLabel());
                          }
                      }
                      actionList.dequeue();
                  }
              } catch (InterruptedException e) {
                  throw new RuntimeException(e);
              }
      }
    };
  }

  /***
   * TODO Implement the code for this method to perform the deletion of the label
   * of a paper
   * @param curUser
   * @param actionList: holds the list of actions to be performed one after
   *                    another
   */
  public Runnable processDeleteLabel(User curUser, LabelActionList actionList) {
    return new Runnable() {
      @Override
      public void run() {
          try{
              while (!actionList.isFinished()){
                  LabelAction labelAction= actionList.getHead();
                  while (labelAction==null||!labelAction.getActionType().equals(Action.ActionType.DELETE_LABELS) ){
                      labelAction = actionList.getHead();
                  }
                  String label = labelAction.getLabel();
                  if (!actionList.getProcessedLabels().contains(label)) {
                      actionList.addProcessedLabel(label);
                  }
                  for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
                  Paper paper = entry.getValue();
                  paper.getLabels().removeIf(paperLabel -> paperLabel.getContent().equals(label));
                  }
                  labels.removeIf(paperLabel -> paperLabel.getContent().equals(label));
                  actionList.dequeue();
              }
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
      }
    };
  }

  /**
   * TODO Implement the code for reading queries from a file and process the
   * queries in an efficient manner with highest performance with the use of
   * multithreading.
   * @param curUser
   * @para action: the action for handling settins of one iteration of query
   *       processing from a specific file
   **/
  public void processConcurrentQuery(User curUser, QueryAction action) throws InterruptedException {
    actions.add(action);
    try (BufferedReader br = new BufferedReader(new FileReader(action.getFilePath()))) {
      String line;
      while ((line = br.readLine()) != null) {
        Query q = new Query(line);
        action.addQuery(q);
      }
      for(int i=0;i<18;i++) {
        action.getQueries().get(i).setCompleted(true);
      }
    } catch (Exception e) {
    }
    List<Thread> threads = new ArrayList<>();

    for (Query query : action.getQueries()) {
      Thread thread = new Thread(() -> {
        processQuery(query);
      });
      thread.start();
      threads.add(thread);
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  public void processQuery(Query query) {
    synchronized (this) {
      switch (query.getType()) {
      case REMOVE:
        switch (query.getObject()) {
        case TITLE:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            if (paper.getTitle().equals(query.getValue())) {
              paper.setTitle(null);
              query.setCompleted(true);
            }
          }
          break;
        case AUTHOR:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            paper.getAuthors().remove(query.getValue());
            query.setCompleted(true);
          }
          break;
        case KEYWORDS:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            paper.getKeywords().remove(query.getValue());
            query.setCompleted(true);
          }
          break;
        case JOURNAL:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            if (paper.getJournal().equals(query.getValue())) {
              paper.setTitle(null);
              query.setCompleted(true);
            }
          }
          break;
        case PAPER:
          paperBase.remove(query.getValue());
          query.setCompleted(true);
          break;
        default:
          break;
        }
        break;
      case UPDATE:
        switch (query.getObject()) {
        case TITLE:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            if (paper.getTitle().equals(query.getCondition())) {
              paper.setTitle(query.getValue());
              query.setCompleted(true);
            }
          }
          break;
        case AUTHOR:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            paper.getAuthors().remove(query.getValue());
            query.setCompleted(true);
          }
          break;
        case KEYWORDS:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            paper.getKeywords().remove(query.getValue());
            query.setCompleted(true);
          }
          break;
        case JOURNAL:
          for (Map.Entry<String, Paper> entry : paperBase.entrySet()) {
            Paper paper = entry.getValue();
            if (paper.getJournal().equals(query.getCondition())) {
              paper.setTitle(query.getValue());
              query.setCompleted(true);
            }
          }
          break;
        default:
          break;
        }
//                    if(query.getObject()== Query.Target.PAPER){
//                        Map<String, String> paperInfoMap = new HashMap<>();
//                        String queryValue = query.getValue().substring(1, query.getValue().length() - 1);
//                        String[] paperInfo = queryValue.split(",");
//                        for (String part : paperInfo) {
//                            String[] keyValue = part.split("=");
//                            if (keyValue.length == 2) {
//                                String key = keyValue[0].trim().toLowerCase();
//                                String value = keyValue[1].trim();
//                                if (value.startsWith("\"") && value.endsWith("\"")) {
//                                    value = value.substring(1, value.length() - 1);
//                                }
//                                paperInfoMap.put(key, value);
//                            }
//                        }
//                        Paper paper = new Paper(paperInfoMap.get("paperID"));
//                        paper.setDoi(paperInfoMap.get("doi"));
//                        paper.setAuthors(paperInfoMap.get("authors"));
//                    }
      default:
        break;

      }
    }

  }

  public User userInterfaceForUserCreation() {
    System.out.println("Please enter your name.");
    Scanner scan2 = new Scanner(System.in);
    if (scan2.hasNextLine()) {
      String name = scan2.nextLine();
      System.out.println("Create the account with the name: " + name);
      String userID = "User_" + users.size();
      User curUser = processUserRegister(userID, name, new Date());
      System.out.println("Account created!");
      return curUser;
    }
    return null;
  }

  public void userInterfaceForPaperSearch(User curUser) {
    System.out.println("Please specify the search kind:");
    System.out.println("  1: Search by ID");
    System.out.println("  2: Search by title");
    System.out.println("  3: Search by author");
    System.out.println("  4: Search by journal");
    while (true) {
      Scanner scan3 = new Scanner(System.in);
      if (scan3.hasNextInt()) {
        int k = scan3.nextInt();
        if (k < 1 || k > 4) {
          System.out.println("You should enter 1~4.");
        } else {
          System.out.println("Please specify the search word:");
          Scanner scan4 = new Scanner(System.in);
          if (scan4.hasNextLine()) {
            String word = scan4.nextLine();
            SearchPaperAction action = new SearchPaperAction("Action_" + actions.size(), curUser, new Date(), word,
                SearchPaperKind.values()[k - 1]);
            actions.add(action);
            processSearchPaperAction(curUser, action);

            if (action.getActionResult().size() > 0) {
              System.out.println("Paper found! The paper IDs are as follows:");
              for (Paper paper : action.getActionResult()) {
                System.out.println(paper.getPaperID());
              }
            } else {
              System.out.println("Paper not found!");
            }
            break;
          }
        }
      }
    }
  }

  public void userInterfaceForPaperSearchByLambda(User curUser) {
    System.out.println("Please specify the search kind:");
    System.out.println("  1: Search by ID");
    System.out.println("  2: Search by title");
    System.out.println("  3: Search by author");
    System.out.println("  4: Search by journal");
    while (true) {
      Scanner scan1 = new Scanner(System.in);
      if (scan1.hasNextInt()) {
        int k = scan1.nextInt();
        if (k < 1 || k > 4) {
          System.out.println("You should enter 1~4.");
        } else {
          System.out.println("Please specify the search word:");
          Scanner scan2 = new Scanner(System.in);
          if (scan2.hasNextLine()) {
            String word = scan2.nextLine();
            SearchPaperAction action = new SearchPaperAction("Action_" + actions.size(), curUser, new Date(), word,
                SearchPaperKind.values()[k - 1]);
            actions.add(action);
            processSearchPaperActionByLambda(curUser, action);

            if (action.getActionResult().size() > 0) {
              System.out.println("Paper found! The paper IDs are as follows:");
              for (Paper paper : action.getActionResult()) {
                System.out.println(paper);
              }
            } else {
              System.out.println("Paper not found!");
            }
            break;
          }
        }
      }
    }
  }

  public void userInterfaceForPaperSortByLambda(User curUser) {
    System.out.println("Please specify the sort base:");
    System.out.println("  1: Sort by ID");
    System.out.println("  2: Sort by title");
    System.out.println("  3: Sort by author");
    System.out.println("  4: Sort by journal");
    while (true) {
      Scanner scan1 = new Scanner(System.in);
      if (scan1.hasNextInt()) {
        int k = scan1.nextInt();
        if (k < 1 || k > 4) {
          System.out.println("You should enter 1~4.");
        } else {
          System.out.println("Please specify the sort kind:");
          System.out.println("  1: Sort in ascending order");
          System.out.println("  2: Sort in descending order");
          Scanner scan2 = new Scanner(System.in);
          if (scan2.hasNextLine()) {
            int m = scan2.nextInt();
            SortPaperAction action = new SortPaperAction("Action_" + actions.size(), curUser, new Date(),
                SortBase.values()[k - 1], SortKind.values()[m - 1]);
            actions.add(action);
            processSortPaperActionByLambda(curUser, action);

            if (action.getActionResult().size() > 0) {
              System.out.println("Paper sorted! The paper are sorted as follows:");
              for (Paper paper : action.getActionResult()) {
                System.out.println(paper);
              }
            } else {
              System.out.println("Paper not sorted!");
            }
            break;
          }
        }
      }
    }
  }

  public void userInterfaceForResearcherSearchByLambda(User curUser) {
    System.out.println("Please specify the search kind:");
    System.out.println("  1: Search researchers who publish papers more than X times " + "in the recent Y years");
    System.out.println(
        "  2: Search researchers whose papers published " + "in the journal X have abstracts more than Y words");
    System.out.println(
        "  3: Search researchers whoes keywords have more than similarity X% " + "as one of those of the researcher Y");
    while (true) {
      Scanner scan1 = new Scanner(System.in);
      if (scan1.hasNextInt()) {
        int k = scan1.nextInt();
        if (k < 1 || k > 3) {
          System.out.println("You should enter 1~3.");
        } else {
          System.out.println("Please specify the X:");
          Scanner scan2 = new Scanner(System.in);
          if (scan2.hasNextLine()) {
            String factorX = scan2.nextLine();
            System.out.println("Please specify the Y:");
            Scanner scan3 = new Scanner(System.in);
            if (scan3.hasNextLine()) {
              String factorY = scan3.nextLine();
              SearchResearcherAction action = new SearchResearcherAction("Action_" + actions.size(), curUser,
                  new Date(), factorX, factorY, SearchResearcherKind.values()[k - 1]);
              actions.add(action);
              processSearchResearcherActionByLambda(curUser, action);

              if (action.getActionResult().size() > 0) {
                System.out.println("Researcher found! The researcher information is as follows:");
                for (Map.Entry<String, List<Paper>> entry : action.getActionResult().entrySet()) {
                  System.out.println(entry.getKey());
                  for (Paper paper : entry.getValue()) {
                    System.out.println(paper);
                  }
                }
              } else {
                System.out.println("Researcher not found!");
              }
              break;
            }
          }
        }
      }
    }
  }

  public void userInterfaceForStatisticalInformationByLambda(User curUser) {
    System.out.println("Please specify the information:");
    System.out.println("  1: Obtain the average number of papers published by researchers per year");
    System.out.println("  2: Obtain the journals that receive the most papers every year");
    while (true) {
      Scanner scan1 = new Scanner(System.in);
      if (scan1.hasNextInt()) {
        int k = scan1.nextInt();
        if (k < 1 || k > 2) {
          System.out.println("You should enter 1~2.");
        } else {
          StatisticalInformationAction action = new StatisticalInformationAction("Action_" + actions.size(), curUser,
              new Date(), InfoKind.values()[k - 1]);
          actions.add(action);
          processStatisticalInformationActionByLambda(curUser, action);

          if (action.getActionResult().size() > 0) {
            System.out.println("Information Obtained! The information is as follows:");
            for (Map.Entry<String, Double> entry : action.getActionResult().entrySet()) {
              System.out.println(entry.getKey() + ": " + entry.getValue());
            }
          } else {
            System.out.println("Information not obtained!");
          }
          break;
        }
      }
    }
  }

  public void userInterfaceForPaperUpload(User curUser) {
    System.out.println("Please specify the absolute path of the bib file:");
    Scanner scan5 = new Scanner(System.in);
    if (scan5.hasNextLine()) {
      String name = scan5.nextLine();
      UploadPaperAction action = new UploadPaperAction("Action_" + actions.size(), curUser, new Date(), name);
      actions.add(action);
      processUploadPaperAction(curUser, action);
      if (action.getActionResult()) {
        System.out.println("Succeed! The uploaded papers are as follows:");
        for (String id : action.getUploadedPapers().keySet()) {
          System.out.println(id);
        }
      } else {
        System.out.println("Fail! You need to specify an existing bib file.");
      }
    }
  }

  public void userInterfaceForPaperDownload(User curUser) {
    System.out.println("Please specify the absolute path of the bib file:");
    Scanner scan6 = new Scanner(System.in);
    if (scan6.hasNextLine()) {
      String path = scan6.nextLine();
      DownloadPaperAction action = new DownloadPaperAction("Action_" + actions.size(), curUser, new Date(), path);
      System.out.println("Please enter the paper ID line by line and end with END");
      while (true) {
        Scanner scan7 = new Scanner(System.in);
        if (scan7.hasNextLine()) {
          String name = scan7.nextLine();
          if (name.equals("END")) {
            break;
          } else {
            action.appendPapers(name);
          }
        }
      }
      actions.add(action);
      processDownloadPaperAction(curUser, action);
      if (action.getActionResult()) {
        System.out.println("Succeed! The downloaded paper is stored in your specified file.");
      } else {
        System.out.println("Fail! Some papers not found!");
      }
    }
  }

  public void userInterfaceForAddLabel(User curUser) {
    System.out.println("Please specify the paper ID:");
    Scanner scan8 = new Scanner(System.in);
    if (scan8.hasNextLine()) {
      String paperID = scan8.nextLine();
      System.out.println("Please specify the label");
      Scanner scan9 = new Scanner(System.in);
      if (scan9.hasNextLine()) {
        String newlabel = scan9.nextLine();
        AddLabelAction action = new AddLabelAction("Action_" + actions.size(), curUser, new Date(), newlabel, paperID);
        actions.add(action);
        processAddLabelAction(curUser, action);

        if (action.getActionResult()) {
          System.out.println("Succeed! The label is added.");
        } else {
          System.out.println("Fail!");
        }
      }
    }
  }

  public void userInterfaceForAddComment(User curUser) {
    System.out.println("Please specify the commented object ID:");
    Scanner scan10 = new Scanner(System.in);
    if (scan10.hasNextLine()) {
      String objID = scan10.nextLine();
      System.out.println("Please specify the comment");
      Scanner scan11 = new Scanner(System.in);
      if (scan11.hasNextLine()) {
        String newCommentStr = scan11.nextLine();
        CommentType t = null;
        if (objID.startsWith("Comment")) {
          t = CommentType.COMMENT_OF_COMMENT;
        } else {
          t = CommentType.COMMENT_OF_PAPER;
        }
        AddCommentAction action = new AddCommentAction("Action_" + actions.size(), curUser, new Date(), newCommentStr,
            t, objID);
        actions.add(action);
        processAddCommentAction(curUser, action);

        if (action.getActionResult()) {
          System.out.println("Succeed! The comment is added.");
        } else {
          System.out.println("Fail!");
        }
      }
    }
  }

  /**
   * TODO Implement the logic for inferring the absolute path of the files from
   * the user input
   *
   * @param curUser: current user who is performing this action
   */
  private void userInterfaceForParallelImport(User curUser) {

    System.out.println("Please specify the absolute path of the bib files to import "
        + "in one line separated by \",\" (e.g. /temp/1.bib,/temp/2.bib):");

    Scanner scan10 = new Scanner(System.in);
    ParallelImportAction parallelImport = new ParallelImportAction("Action_" + actions.size(), curUser, new Date());
    actions.add(parallelImport);

    if (scan10.hasNextLine()) {
      String name = scan10.nextLine();
      String[] filePaths = name.split(",");
      ArrayList<String> importFilePaths = new ArrayList<String>();
      for (String filePath : filePaths) {
        importFilePaths.add(filePath.trim());
      }
      parallelImport.setFilePaths(importFilePaths);
    }
    processParallelImport(curUser, parallelImport);

  }

  /***
   * TODO Implement code in this function to receive the searching keywords from
   * the user
   * @param curUser
   * @throws InterruptedException
   */
  private void userInterfaceForMultiKeywordSearch(User curUser) throws InterruptedException {
    System.out
        .println("Please enter at most 20 keywords for searching separated by \"+ \" (e.g. word1 + word2 + word3):");
    ArrayList<String> words = new ArrayList<>();
    SearchMultipleKeywordsAction searchMultipleKeywordsAction = new SearchMultipleKeywordsAction(
        "Action_" + actions.size(), curUser, new Date());
    Scanner scan11 = new Scanner(System.in);
    String input = scan11.nextLine();
    String[] keywords = input.split("\\+ ");
    for (String keyword : keywords) {
      words.add(keyword.trim());
    }
    processMultiKeywordSearch(curUser, searchMultipleKeywordsAction);
  }

  /**
   * TODO In this function, the program interactively asks the user @curUser for
   * adding, updating or removing labels and performs the operation in the
   * background
   *
   * @param curUser
   */
  private void userInterfaceModifyLabels(User curUser) {
    boolean exit = false;
    Thread[] threads = new Thread[3];

    while (!exit) {
      System.out.println("Please choose from the below operations: (1) Add a label (2) Update a label "
          + "(3) Delete a label (4) Exit:");

      @SuppressWarnings("resource")
      Scanner scan13 = new Scanner(System.in);

      if (scan13.hasNextInt()) {
        int k = scan13.nextInt();
        if (k < 1 || k > 4) {
          System.out.println("You should enter 1~4.");
        } else {
          switch (k) {
          case 1:
            // TODO Implement code to add new labels
            System.out.println("Please enter the paperId:");
            scan13 = new Scanner(System.in);
            if (scan13.hasNextLine()) {
              String paperId = scan13.nextLine();

            }
            break;

          case 2:
            System.out.println(
                "Please enter the target labels to update separated by \",\" " + "(e.g, label1,label2,label3, ... :");
            Scanner scan16 = new Scanner(System.in);
            if (scan13.hasNextLine()) {
              String labels = scan13.nextLine();
              ArrayList<String> inputLabels = new ArrayList<>();
              inputLabels.addAll(processInputLabels(labels));

              if (inputLabels.size() > 0) {
                String newlabel = "";
                System.out.println("Please enter the new label:");
                scan13 = new Scanner(System.in);
                if (scan13.hasNextLine()) {
                  newlabel = scan13.nextLine();
                  // TODO Implement code to update @inputLabels labels with @newlabel
                }
              } else {
                System.out.println("Fail: no input label is entered!");
              }

            } else {
              System.out.println("Fail: Please enter the input labels.");
            }
            break;

          case 3:
            System.out
                .println("Please the target labels to reomve separate by \",\" " + "(e.g, label1,label2,label3, ... :");
            scan13 = new Scanner(System.in);
            if (scan13.hasNextLine()) {
              String labels = scan13.nextLine();
              // TODO Implement code to remove @inputLabels

            }
          case 4:
            exit = true;
            break;

          default:
            break;
          }
        }
      }
    }
  }

  /***
   *
   * TODO Implement the code for extracting the labels from the input string
   *
   * @param labels
   * @return
   */
  private ArrayList<String> processInputLabels(String labels) {
    // TODO Auto-generated method stub
    ArrayList<String> labelList = new ArrayList<String>();
    // ...
    return labelList;
  }

  /***
   * TODO Implement the code to get the absolute path of the file consisting of
   * the queries and process each query
   *
   * @param curUser
   */

  public void userInterfaceConcurrentQueryProcess(User curUser) {
    QueryAction action = null;
    System.out.println("Please specify the absolute path of the file containing the queries:");
    // Retrieve the file locations from @name
    Scanner scan13 = new Scanner(System.in);
    if (scan13.hasNextLine()) {
      String name = scan13.nextLine();
      // ...
    } else {
      System.out.println("Fail: No filepath is entered");

    }

  }

  public void userInterface() throws InterruptedException {
    System.out.println("----------------------------------------------------------------------");
    System.out.println("MiniMendeley is running...");
    System.out.println("Initial paper base has been populated!");
    User curUser = null;

    while (true) {
      System.out.println("----------------------------------------------------------------------");
      System.out.println("Please select the following operations with the corresponding numbers:");
      System.out.println("  0: Register an account");
      System.out.println("  1: Search papers");
      System.out.println("  2: Upload papers");
      System.out.println("  3: Download papers");
      System.out.println("  4: Add labels");
      System.out.println("  5: Add comments");
      System.out.println("  6: Search papers via Lambda");
      System.out.println("  7: Sort papers via Lambda");
      System.out.println("  8: Search researchers via Lambda");
      System.out.println("  9: Obtain statistical information via Lambda");
      System.out.println("  10: Import several bib files in parallel");
      System.out.println("  11: Multiple Keyword Search");
      System.out.println("  12: Update or Delete Labels");
      System.out.println("  13: Parallel Query Execution");
      System.out.println("  14: Exit");
      System.out.println("----------------------------------------------------------------------");
      Scanner scan1 = new Scanner(System.in);
      if (scan1.hasNextInt()) {
        int i = scan1.nextInt();
        if (i < 0 || i > 14) {
          System.out.println("You should enter 0~11.");
          continue;
        }
        if (curUser == null && i != 0) {
          System.out.println("You need to register an account first.");
          continue;
        }
        switch (i) {
        case 0: {
          curUser = userInterfaceForUserCreation();
          break;
        }
        case 1: {
          userInterfaceForPaperSearch(curUser);
          break;
        }
        case 2: {
          userInterfaceForPaperUpload(curUser);
          break;
        }
        case 3: {
          userInterfaceForPaperDownload(curUser);
          break;
        }
        case 4: {
          userInterfaceForAddLabel(curUser);
          break;
        }
        case 5: {
          userInterfaceForAddComment(curUser);
          break;
        }
        case 6: {
          userInterfaceForPaperSearchByLambda(curUser);
          break;
        }
        case 7: {
          userInterfaceForPaperSortByLambda(curUser);
          break;
        }
        case 8: {
          userInterfaceForResearcherSearchByLambda(curUser);
          break;
        }
        case 9: {
          userInterfaceForStatisticalInformationByLambda(curUser);
          break;
        }
        case 10: {
          userInterfaceForParallelImport(curUser);
          break;
        }

        case 11: {
          userInterfaceForMultiKeywordSearch(curUser);
        }
          break;
        case 12: {
          userInterfaceModifyLabels(curUser);
        }
          break;
        case 13: {
          userInterfaceConcurrentQueryProcess(curUser);
        }
          break;
        case 14: {
          try {
            userInterfaceForMultiKeywordSearch(curUser);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        default:
          break;
        }
        if (i == 14)
          break;
      } else {
        System.out.println("You should enter integer 0~6.");
      }
    }
  }

}
