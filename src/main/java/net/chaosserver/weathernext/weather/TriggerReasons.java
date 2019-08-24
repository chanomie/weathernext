package net.chaosserver.weathernext.weather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Object to describe the reasons a scheduled alert got triggered.
 * 
 * @author jreed
 */
public class TriggerReasons implements Serializable {
	/** Unique Identifier of the Trigger */
	protected String triggerReasonsId;
	
	/** The list of reasons an alert was scheduled. */
	protected Collection<TriggerReason> triggerReasons;
	
	/** No arg constructure required for serialization. */
	public TriggerReasons() {
		
	}
	
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
	
	/**
	 * Gets the list of trigger reasons.
	 * 
	 * @return The list of trigger reasons
	 */
	public Collection<TriggerReason> getTriggerReasons() {
		// TODO - sort the list by date
		return this.triggerReasons;
	}
	
	
	/**
	 * Debug version of a toString()
	 * 
	 * @return debug string
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("TriggerReasons [triggerReasonsId=["
				+ this.triggerReasonsId + "], "
				+ "triggerReasons=[");
				
        for (TriggerReason triggerReason : this.triggerReasons) {
        	sb.append("[triggerDate=");
        	sb.append(triggerReason.getTriggerDate());
        	sb.append(",triggerReasons=");
        	sb.append(triggerReason.getTriggerReason());
        	sb.append("]");
		}
				
		sb.append("]]");
		
		return sb.toString();
	}
}
