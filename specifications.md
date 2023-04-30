# Mini-Mendeley

This document demonstrates the specification of the system.
In this project, you are required to improve the previous paper management system **Mini-Mendeley**.
It should support the following functionalities:

- Parallel importing of bib files
- Rapid Multiple keywords search
- Concurrent Modifications on Labels
- Efficient Query Processing

Most of the classes are the same as the ones in PA1 and PA2. In what follows, we provide more concrete specifications for the methods you need to implement in this project.
You also provide several illustrative figures in PA3.pdf to help you understand the specifications.

## Parallel Import of bib Files
You should add a new functionality to import multiple bib files in parallel. Fully implement the functional interfaces of the class `ParallelImportAction` in order to add the functionality of importing multiple bib files in parallel. Utilize the method `userInterfaceForParallelImport` to receive the absolute path of the files from the user. Add all the files stored in  `importedPapers` to  `paperBase` data structure used for storing the papers. ​ Note that a user can only import 10 files one at a time. Implement the function `processParallelImport` to perform the importing of the bib files in parallel and store the results in `importedPapers`. You can only use lock, wait, notify and notifyAll to synchronize the threads. You must ensure that all the threads have equal access to the `importedPapers`. 


## Rapid Multiple Keywords Search
You should add a new functionality to search multiple keywords rapidly​. Fully implement the functional interfaces of the class `SearchMultipleKeywordsAction` in order to add the functionality of rapidly searching for several keywords in title, abstract or keywords of papers. Create an ArrayList named `results`  in  `SearchMultipleKeywordsAction` class to store the results of the search. Utilize the method `userInterfaceForParallelImport` to receive the words from the user. Create an instance of `SearchMultipleKeywordsAction` for simultaneously searching the papers for the words. Note that a user can only search for 20 words one at a time. Implement the function `processMultiKeywordSearch` to create multiple threads to perform the search and save the results in `results`. You can only use 5 threads for the searching process. Try to use all the threads effectively so as to perform the search operations with high performance. You should store the results in `results` arraylist that is shared between the threads. You can only use semaphore, lock, wait, notify and notifyAll to synchronize the threads. You must ensure that all the threads have equal access to the `results` arraylist.


## Concurrent Modifications on Labels
You should add a new functionality to allow the user to add, update or delete labels. Implement the functional interfaces of the class `LabelActions` to add a new kind of action to the MiniMendeley to perform add, update or remove operations on labels. Utilize the method `userInterfaceModifyLabels` in the MiniMendelyEngine class to receive the type of the action with inputs from the user and uses three separate threads for performing the add, update and remove operations in parallel. In this function, the `actionList` is a list used to store an action on a label. Implement  the functions `processAddLabel`, `processUpdateLabel` and `processDeleteLabel` to perform the three kinds of actions on the labels individually.  Please note that you need to create three background threads for performing any of the mentioned actions on labels. So, the user can interact with the program and perform more actions interactively. You must also implement the functions in `User` class for updaing the label list for a user. Furthermore, use `LabelActionList` to keep the track of actions performed on labels. Note tat once an action is completed, the user must be notified.

You can only use semaphore, lock, wait, notify and notifyAll to synchronize the threads. You must ensure that all the threads have equal access to the shared data structures.

## Efficient Query Processing
You should add a new functionality to read from a large file and process each query concurrently. Fully implement the functional interfaces of the class `QueryAction` in order to add the necessary functionalities for reading a file that contains a series of queries to perform on the currently imported papers efficiently. The class `Query` is used to represent a query which can be an add, update or remove operation. It can be in any of the three form below:

1. ADD,object, value 
2. UPDATE, object, condition, value 
3. REMOVE, object, condition

An `action` in a query indicates the operation to be perfomred. An `object` refers to the kind of object, either a paper or an elment of it, that is target of the query. And, `value` includes the value of the object that must be added or deleted. Condition refers to the condition of the object to be updated. Utilize the method `userInterfaceConcurrentQueryProcess` to receive the absolute path of the file containing the queries. Implement the function `processConcurrentQuery` to process a query. Note that you can use as many threads as you want for this task. However, your must ensure that your implementation satisfies the highest efficiency in processing all the queries within a file with thousands of queries. A sample file is stored in `resources/query.txt` for your reference. You can only use semaphore, lock, wait, notify and notifyAll to synchronize the threads. You must ensure that all the threads have equal access to the `results` arraylist.

## Some Remarks
Lastly, it should be noting that your code will be tested by running our testcases rather than testing via the console manually.
Therefore, you should make sure that: (1) your code can be complied succesfully;
(2) your implementation can pass the public testcases we provided in `src/test`;
(3) submit this project without changing any directory structures, e.g., avoid removing `src/test`.
However, passing all the public testcases does not mean that you can obtain the full mark for the PA.
We will also provide many additional testcases as the hidden ones,
which are different from the ones we provided in this skeleton.
