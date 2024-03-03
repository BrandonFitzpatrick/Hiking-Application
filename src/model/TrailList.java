package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

public class TrailList implements Serializable {
	private HashMap<String, LinkedList<Trail>> trailsByName;
	private HashMap<Double, LinkedList<Trail>> trailsByLength;
	private HashMap<Double, LinkedList<Trail>> trailsByElevation;
	private HashMap<Difficulty, LinkedList<Trail>> trailsByDifficulty;
	private HashMap<RouteType, LinkedList<Trail>> trailsByRouteType;
	
	private int count = 0;
	
	//variables keeping track of the smallest and largest lengths of any trail inside the list
	private double minimumLength = Double.MAX_VALUE;
	private double maximumLength = Double.MIN_VALUE;
	private double minimumElevation = Double.MAX_VALUE;
	private double maximumElevation = Double.MIN_VALUE;
	
	private static TrailList trailList;
	
	public static TrailList getTrailList() {
		if (trailList == null) {
			trailList = new TrailList();
		}
		return trailList;
	}
	
	public static void setTrailList(TrailList trailList) {
		TrailList.trailList = trailList;
	}
	
	private TrailList() {
		//trailsByName will contain 50,000 trails at most, but additional leeway is provided to limit rehashing
		trailsByName = new HashMap<>(75000);
		//trailsByLength and trailsByElevation will contain less than 50,000 trails, 
		//but additional leeway is provided to limit rehashing
		trailsByLength = new HashMap<>(50000);
		trailsByElevation = new HashMap<>(50000);
		//trailsByDifficulty and trailsByRouteType will only contain three items at most (one for each difficulty and type list),
		//but additional leeway is provided to limit rehashing
		trailsByDifficulty = new HashMap<>(5);
		trailsByRouteType = new HashMap<>(5);
	}
	
	public void add(Trail trail) {
		if(count < 50000) {
			//increment count each time a trail gets added
			//once it hits 50000, no more trails can be added
			count++;
			
			if(trail.getLength() < minimumLength) {
				minimumLength = trail.getLength();
			} else if (trail.getLength() > maximumLength) {
				maximumLength = trail.getLength();
			}
			
			if(trail.getElevation() < minimumElevation) {
				minimumElevation = trail.getElevation();
			} else if (trail.getElevation() > maximumElevation) {
				maximumElevation = trail.getElevation();
			}
			
			//if a trail with this name is not already within the hash table, a new LinkedList will be created and added to the table,
			//and the trail will be added to that LinkedList
			//otherwise, the trail is simply added to the already existing linked list
			//same applies to all of the other hash tables based on their key
			if (!trailsByName.containsKey(trail.getName())) {
				trailsByName.put(trail.getName(), new LinkedList<Trail>());
			}
	        trailsByName.get(trail.getName()).add(trail);
	        
	        //for trails by length, the key is the length of the trail rounded down by increments of 0.5
	        if(!trailsByLength.containsKey(new Double(roundOff(trail.getLength(), 0.5)))) {
	        	trailsByLength.put(new Double(roundOff(trail.getLength(), 0.5)), new LinkedList<Trail>());
	        }
	        trailsByLength.get(roundOff(trail.getLength(), 0.5)).add(trail);
	        
	        //for trails by elevation, the key is the length of the trail rounded down by increments of 1
	        if(!trailsByElevation.containsKey(new Double(roundOff(trail.getElevation(), 1)))) {
	        	trailsByElevation.put(new Double(roundOff(trail.getElevation(), 1)), new LinkedList<Trail>());
	        }
	        trailsByElevation.get(roundOff(trail.getElevation(), 1)).add(trail);
	        
	        if (!trailsByDifficulty.containsKey(trail.getDifficulty())) {
				trailsByDifficulty.put(trail.getDifficulty(), new LinkedList<Trail>());
			}
	        trailsByDifficulty.get(trail.getDifficulty()).add(trail);
	        
	        if (!trailsByRouteType.containsKey(trail.getRouteType())) {
				trailsByRouteType.put(trail.getRouteType(), new LinkedList<Trail>());
			}
	        trailsByRouteType.get(trail.getRouteType()).add(trail);
		}
	}
	
	public void delete(Trail trail) {
        if(trailsByName.containsKey(trail.getName())) {
        	//decrement count each time a trail gets removed
        	count--;
        	
        	//remove the trail from its respective LinkedList
            trailsByName.get(trail.getName()).remove(trail);
            //remove the entire LinkedList if it's empty
            if(trailsByName.get(trail.getName()).size() == 0) {
            	trailsByName.remove(trail.getName());
            }

            //remove the trail from its respective LinkedList
            trailsByLength.get(roundOff(trail.getLength(), 0.5)).remove(trail);
            //remove the entire LinkedList if it's empty
            if(trailsByLength.get(roundOff(trail.getLength(),.5)).size() == 0) {
            	trailsByLength.remove(roundOff(trail.getLength(),.5));
            }
            
            //remove the trail from its respective LinkedList
            trailsByElevation.get(roundOff(trail.getElevation(), 1)).remove(trail);
            //remove the entire LinkedList if it's empty
            if(trailsByElevation.get(roundOff(trail.getElevation(), 1)).size() == 0) {
            	trailsByElevation.remove(roundOff(trail.getElevation(),1));
            }

            //remove the trail from its respective LinkedList
            trailsByDifficulty.get(trail.getDifficulty()).remove(trail);
            //remove the entire LinkedList if it's empty
            if(trailsByDifficulty.get(trail.getDifficulty()).size() == 0) {
            	trailsByDifficulty.remove(trail.getDifficulty());
            }

            //remove the trail from its respective LinkedList
            trailsByRouteType.get(trail.getRouteType()).remove(trail);
            //remove the entire LinkedList if it's empty
            if(trailsByRouteType.get(trail.getRouteType()).size() == 0) {
            	trailsByRouteType.remove(trail.getRouteType());
            }
        }
    }
	
	//rounds numbers down to a specified value
	private static double roundOff(double number, double roundOffValue) {
		return Math.floor(number * (1.0 / roundOffValue)) * roundOffValue;
	}
	
	public List<Trail> search(String name, Double minLength, Double maxLength, Double minElevation, Double maxElevation, ObservableList<Difficulty> difficulty, ObservableList<RouteType> routeType) {
		//mainList contains each list of results for each individual search criteria
		LinkedList<LinkedList<LinkedList<Trail>>> mainList = new LinkedList<>(); 
		int min = Integer.MAX_VALUE;
		boolean filteringByName = false;
		LinkedList<LinkedList<Trail>> smallestList = null;
		
		//if the user doesn't specify a minimum length, the minimum length for searching will be the smallest length in the list
		if(minLength == null) {
			minLength = minimumLength;
		}
		
		//if the user doesn't specify a maximum length, the maximum length for searching will be the largest length in the list
		if (maxLength == null) {
			maxLength = maximumLength;
		} 
		
		//if the user doesn't specify a minimum or maximum elevation, the minimum and/or maximum elevation
		//will be the smallest and/or largest elevations in the list
		if(minElevation == null && maxElevation != null) {
			minElevation = minimumElevation;
		} else if (minElevation != null && maxElevation == null) {
			maxElevation = maximumElevation;
		}
		
		final Double minLengthCopy = minLength, maxLengthCopy = maxLength, minElevationCopy = minElevation, maxElevationCopy = maxElevation;
		
		//if only name is included in the search, then we can filter by the name list, which will most likely be extremely small, 
		//and may only contain a single item, which means filtering would be O(1) 
		LinkedList<Trail> nameList = new LinkedList<>();
		if(!name.equals("")) {
			nameList = trailsByName.get(name);
			min = nameList.size();
			filteringByName = true;
		}
		
		//for each search criteria entered, the list of results is added to the main list, and the smallest list
		//contained within that main list is set, which will be used for filtering, ensuring that the filter
		//has to iterate through as few items as possible
		if(minLength != null && maxLength != null) {
			int currentSize = addToMainList(mainList, minLength, maxLength, 0.5, trailsByLength);
			if(currentSize < min) {
				min = currentSize;
				smallestList = mainList.getLast();
				filteringByName = false;
			}
		}
		
		if(minElevation != null && maxElevation != null) {
			int currentSize = addToMainList(mainList, minElevation, maxElevation, 1.0, trailsByElevation);
			if(currentSize < min) {
				min = currentSize;
				smallestList = mainList.getLast();
				filteringByName = false;
			}
		}
		
		if(difficulty != null && !difficulty.isEmpty()) {
			int currentSize = addToMainList(mainList, difficulty, trailsByDifficulty);
			if(currentSize < min) {
				min = currentSize;
				smallestList = mainList.getLast();
				filteringByName = false;
			}
		}
		
		if(routeType != null && !routeType.isEmpty()) {
			int currentSize = addToMainList(mainList, routeType, trailsByRouteType);
			if(currentSize < min) {
				min = currentSize;
				smallestList = mainList.getLast();
				filteringByName = false;
			}
		}
	
		if(filteringByName) {
			return nameList.stream().filter(p -> (name.equals("") || p.getName().equals(name)) && (difficulty == null || difficulty.isEmpty() || difficulty.contains(p.getDifficulty())) 
					&& (routeType == null || routeType.isEmpty() || routeType.contains(p.getRouteType())) 
					&& (minLengthCopy == null || maxLengthCopy == null || (minLengthCopy <= p.getLength() && maxLengthCopy >= p.getLength())) 
					&& (minElevationCopy == null || maxElevationCopy == null || (minElevationCopy <= p.getElevation() && maxElevationCopy >= p.getElevation()))).collect(Collectors.toList());
		} else {
			LinkedList<Trail> result = new LinkedList<>();
			for(LinkedList<Trail> trailList : smallestList) {
				result.addAll(trailList.stream().filter(p -> (name.equals("") || p.getName().equals(name)) && (difficulty == null || difficulty.isEmpty() || difficulty.contains(p.getDifficulty())) 
						&& (routeType == null || routeType.isEmpty() || routeType.contains(p.getRouteType())) 
						&& (minLengthCopy == null || maxLengthCopy == null || (minLengthCopy <= p.getLength() && maxLengthCopy >= p.getLength())) 
						&& (minElevationCopy == null || maxElevationCopy == null || (minElevationCopy <= p.getElevation() && maxElevationCopy >= p.getElevation()))).collect(Collectors.toList()));
			}
			return result;
		}
	}
	
	private static <T> int addToMainList(LinkedList<LinkedList<LinkedList<Trail>>> mainList, ObservableList<T> inputs, HashMap<T, LinkedList<Trail>> map) {
		LinkedList<LinkedList<Trail>> temp = new LinkedList<>();
		int currentSize = 0;
		for(T value : inputs) {
			LinkedList<Trail> temp2 = map.get(value);
			currentSize += temp2.size();
			temp.add(temp2);
		}
		mainList.add(temp);
		return currentSize;
	}
	
	private static int addToMainList(LinkedList<LinkedList<LinkedList<Trail>>> mainList, Double min, Double max, Double step, HashMap<Double,LinkedList<Trail>> map) {
        LinkedList<LinkedList<Trail>> temp = new LinkedList<>();
        int currentSize = 0;
        for(double i = roundOff(min, step); i < max; i += step) {
            LinkedList<Trail> temp2 = map.get(i);
            if(temp2 != null) {
                currentSize += temp2.size();
                temp.add(temp2);
            }
        }
        mainList.add(temp);
        return currentSize;
    }
	
	public boolean isEmpty() {
		return count == 0;
	}
	
	//for debugging
	public void display() {
		System.out.println("name: " + trailsByName.toString());
		System.out.println("length: " + trailsByLength.toString());
		System.out.println("elevation: " + trailsByElevation.toString());
		System.out.println("difficulty: " + trailsByDifficulty.toString());
		System.out.println("route type: " + trailsByRouteType.toString());
	}
}
