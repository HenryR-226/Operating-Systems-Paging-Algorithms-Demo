/**
 * @author Henry Rheault
 * 11/09/2019
 * ICS 462 Assignment #5
 * This class contains a FIFO, LFU and OPT methods for page referencing.
 * Also some helper methods such as randomPage, Input, search, and nextPage.
 * All algorithms are called on 3 strings, assuming 1 - 7 frames.
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Page replacement class. It contains all the Page replacement methods and my main method.
 */
public class PageReplacement462 {

	String reference = ""; 								//used to ref the page reference string
	static String randomPageString = ""; 				//used for the random string
	static int randomPageArray[] = new int[20]; 		//used for the random string
	int frameSize = 0; 									//frame size initialized to zero
	int pageFaults = 0; 								//page faults initialized to zero
	char frame[]; 
	int frameCount = 0; 								//frame count initialized to zero
	
	static int referenceStringArray[] = 
		{0,7,1,1,2,0,8,9,0,3,0,4,5,6,7,0,8,9,1,2}; 		//reference string 1 in array form

	static String referenceString = 
		"07112089030456708912"; 						// reference string 1 in string form

	static int referenceString2Array[] = 
		{7,0,1,2,0,3,0,4,2,3,0,3,2,1,2,0,1,7,0,1}; 		//reference string 2 in array form

	static String refString2String = 
		"70120304230321201701"; 						// reference string 2 in string form

	static PrintWriter output; 							//For output 
	
	/**
	 * This is my main method. It starts by calling the random page method to create a random page reference string,
	 * it then calls the fifo, lfu, and opt methods and calculates the page faults for 1 - 7 frames.
	 * It also does the same for 2 other strings that I manually pass.
	 * @param args
	 */
	public static void main(String args[]){

		PageReplacement462 page = new PageReplacement462(); 		//creates new instance
		page.randomPage(); 								//calls random page method
		
		try {
			output = new PrintWriter("HenryRheault462As5Output.txt"); 	//Names output file
			output.println("Henry Rheault"); 			//Prints Name to file to file
			output.println("ICS 462 Assignment #5"); 	//Prints Assignment title to file
			output.println("11/09/2019"); 				//Prints date to file
			output.println(""); 						//Prints empty Line to file
			
		} catch (FileNotFoundException e) { 			//File not Found exception.

			e.printStackTrace();
		}
	
		output.println("Random Reference String: ");
		for(int  i = 1; i < 8; i ++)	{ 				//loops 7 times
			output.println("For " + i + 
		" page frames, and using string page reference string " + 
					randomPageString); 					//prints header line for each frame

			output.println("	FIFO had " + fifo(randomPageArray, i) + 
					" Page Faults."); 					// prints number of faults for FIFO

			output.println("	LFU  had " + 
			lfu(randomPageArray, i) + " Page Faults.");	// prints number of faults for LFU

			page.input(randomPageString, i); 			//creates the input for the opt method.

			output.println("	Optimal  had " + 
				page.opt() + " Page Faults.");			// prints number of faults for opt
		}
		
		output.println("");
		output.println("Reference String 1: ");

		for(int  i = 1; i < 8; i ++)	{
			output.println("For " + i + 
		" page frames, and using string page reference string " + 
					referenceString); 					//prints header line for each frame

			output.println("	FIFO had " + fifo(referenceStringArray, i) 
			+ " Page Faults."); 						// prints number of faults for FIFO

			output.println("	LFU  had " +
			 lfu(referenceStringArray, i) + " Page Faults.");	// prints number of faults for LFU

			page.input(referenceString, i); 			//creates the INPUT for the opt method.

			output.println("	Optimal  had " +
			 page.opt() + " Page Faults.");				// prints number of faults for opt
		}
		
		output.println("");
		output.println("Reference String 2: ");
		for(int  i = 1; i < 8; i ++)	{

			output.println("For " + i + 
		" page frames, and using string page reference string " + 
		refString2String); 								//prints header line for each frame

			output.println("	FIFO had " 
			+ fifo(referenceString2Array, i) + " Page Faults.");  // prints number of faults for FIFO

			output.println("	LFU  had " + 
			lfu(referenceString2Array, i) + " Page Faults.");	// prints number of faults for LFU

			page.input(refString2String, i); 			//creates the INPUT for the opt method.

			output.println("	Optimal  had " + 
			page.opt() + " Page Faults.");				// prints number of faults for opt
		}
		output.close();
	}
	
	/**
	 * This method is used in the optimal page replacement method.
	 * it looks at the remaining string and determines the optimal
	 * next page.
	 * @param page
	 * @param index
	 * @return
	 */
	public int nextPage(char page, int index){
		int result = reference.length() + 1; 			//result for next page
		
		for(int i = index + 1; i < reference.length(); i++){
			char temp = reference.charAt(i);

			if(temp == page){ 		 					//checks if temp equals page
				if(i < result){ 	  					//checks if i is less than result
					result = i; 	  					//sets result to i
				}
			}
		}
		return result; 				  					//return
	}

	/**
	 * This is the optimal page replacement method. It works as follows:
	 * If the page is already there, it increments page fault counter
	 * If page is not there, it finds a page that is not referenced in the future. 
	 * If it finds such a page, it replaces the page with new page. 
	 * If not, it finds a page referenced farthest in future and Replaces it.
	 * @return pageFaults
	 */
	public int opt(){

		for(int i = 0; i < reference.length(); i++){ 	//loops the size of reference string
			char page = reference.charAt(i); 			//takes a number from the string
			if(search(page)){ 							//calls search method
				
			}else{
				pageFaults++; 							//increase page faults

				if(frameCount < frameSize){ 			//compares frame count and frame size
					frame[frameCount] = page; 			//set frame to number pulled from string
					frameCount++;						// increase frame count
				}else{
					int opt[] = new int[frameSize]; 	//creates optimal array size of frame size
					for(int j = 0; j < frameSize; j++){ //loops until j is equal to frame size
						opt[j] = nextPage(frame[j], i); //calls next Page method on frame
					}
					int location = 0; 					//location set to 0
					int maximum = opt[0]; 
					for(int j = 1; j < frameSize; j++){ //loops through frame size
						if(opt[j] > maximum){ 			//if opt[j] is greater than maximum
							maximum = opt[j]; 			//set opt[j] to become the new maximum
							location = j; 	  			//set location to j.
						}
					}
					frame[location] = page; 	  		//set frame[location] to page.
				}
			}
		}
		return pageFaults; 					 			//returns number of page faults
	}
	
	/**
	 * This is the FIFO method, it takes the pageArray and the number of frames as parameters.
	 * It then performs the FIFO algorithm and counts the number of page faults.
	 * @param pageArray
	 * @param numFrames
	 * @return
	 */
	public static int fifo(int pageArray[], int numFrames) { 

		HashSet<Integer> setPages = new HashSet<>(numFrames); 
		Queue<Integer> fifoQueue = new LinkedList<>() ;  	// This will help store pages in the fifo order.

		int numPageFaults = 0; 								//Page fault counter starts at zero.

		for (int i=0; i < 20; i++) {
			if (setPages.size() < numFrames) {  			// Compares the size with the number of frames to see if it can hold more pages 
				if (!setPages.contains(pageArray[i])) {  
					setPages.add(pageArray[i]);
					fifoQueue.add(pageArray[i]);  			// adds page into queue
					numPageFaults++; 						// increment page fault counter
				}
			}
			else{ 											// If the set is full then remove the first page of the queue
				if (!setPages.contains(pageArray[i])) {  	// checks if current page is im set
					int num = fifoQueue.peek(); 			//removes first page	       
					fifoQueue.poll();                         
					setPages.remove(num);                       
					setPages.add(pageArray[i]); 				// adds the current page     
					fifoQueue.add(pageArray[i]); 			// adds current page into queue                      
					numPageFaults++; 						// Increment page fault counter
				}
			}
		}
		return numPageFaults; //returns number of page faults
	}

	
	/**
	 * This is my LFU Method. Its takes page array and the number of frames as parameters
	 * it then performs the LFU algorithm and counts the number of page faults
	 * @param pageArray
	 * @param numFrames
	 * @return
	 */
	public static int lfu(int pageArray[], int numFrames) {

		ArrayList<Integer> pageSet = 
				new ArrayList<>(numFrames); 				//array list of the page set

		int count = 0; 										//counter
		int numPageFaults = 0; 								//keeps track of number of page faults

		for(int i : pageArray)	{ 							//loops through the page array
			if(!pageSet.contains(i))	{ 					//checks to see if page set contains i
				if(pageSet.size() == numFrames)	{ 			//if page set is full 
					pageSet.remove(0); 						//remove
					pageSet.add(numFrames - 1, i); 			//add
				} else
					pageSet.add(count, i); 					//add
				++count; 									//pre-increment count
				numPageFaults++; 							//increment number page faults
				
			} else	{
				pageSet.remove((Object) i); 				//remove
				pageSet.add(pageSet.size(), i); 			//add current page    
			}
		} 
		return numPageFaults; 								//return number of page faults
	}

	/**
	 * This method creates a random generated page string.
	 * For some methods stored as a string, others as an array.
	 */
	public void randomPage() {
		Random rand; 										//random variable

		for(int i = 0; i < 20; i++){ 						//loops 20 times
			rand = new Random(); 							//new random
			int randInt = rand.nextInt(10); 				// number 0 -9
			randomPageArray[i] = randInt; 					//sets number in array

			randomPageString = 
				(randomPageString + randInt); 				//uses number to build a string
		}
	}
	
	/**
	 * This method builds my input for the optimal page replacement method
	 * @param pageString
	 * @param numFrames
	 */

	public void input(String pageString, int numFrames){
		frameCount = 0; 									//frame count set to 0
		pageFaults = 0; 									// page faults set to 0
		reference = pageString; 							//ref string equals passed page string
		frameSize = numFrames; 								//frame size equals num frames
		frame = new char[frameSize];
	}

	
	/**
	 * This method is used in the optimal page replacement method.
	 * It searches and returns a boolean based on if it finds that int or not.
	 * @param ch
	 * @return boolean
	 */
	public boolean search(char ch){	
		for(int i = 0;i < frameCount; i++){ 				//loops frame count
			char temp = frame[i];
			if(temp == ch){ 								//checks is temp equals ch in string
				return true; 								//true
			}
		}
		return false; 										//false
	}
}