package net.chaosserver.weathernext.weather;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Object to describe the reasons a scheduled alert got triggered.
 * 
 * @author jreed
 */
public class TriggerReasons {
	/** Unique Identifier of the Trigger */
	protected String triggerReasonsId;
	
	/** The list of reasons an alert was scheduled. */
	protected Collection<TriggerReason> triggerReasons;
	
	/** Constructs a unique object with a unique id. */
	public TriggerReasons(String triggerReasonsId) {
		this.triggerReasonsId = triggerReasonsId;
		triggerReasons = new ArrayList<TriggerReason>();
	}
	
	/**
	 * Returns the unique key for the object.
	 * @return unique key for the object
	 */
	public String getTriggerReasonsId() {
		return this.triggerReasonsId;
	}
	
	/** Adds a new reason to the list. */
	public void addTriggerReason(TriggerReason triggerReason) {
		triggerReasons.add(triggerReason);
	}
}
