package net.chaosserver.weathernext.weather;

import java.io.Serializable;
import java.util.Date;

/**
 * Indicates the reason a schedule alert is being triggered to send.
 * 
 * @author jreed
 */
public class TriggerReason implements Serializable {
  protected Date triggerDate;
  protected String triggerReason;
  
  /** No arg constructor for serialization. */
  public TriggerReason() {
	  
  }
  
  public TriggerReason(Date triggerDate, String triggerReason) {
	  this.triggerDate = triggerDate;
	  this.triggerReason = triggerReason;
  }
  
  public Date getTriggerDate() {
	  return this.triggerDate;
  }
  
  public String getTriggerReason() {
	  return this.triggerReason;
  }
}
