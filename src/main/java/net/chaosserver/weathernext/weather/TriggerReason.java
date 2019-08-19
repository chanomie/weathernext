package net.chaosserver.weathernext.weather;

import java.util.Date;

/**
 * Indicates the reason a schedule alert is being triggered to send.
 * 
 * @author jreed
 */
public class TriggerReason {
  protected Date triggerDate;
  protected String triggerReason;
  
  public TriggerReason(Date triggerDate, String triggerReason) {
	  this.triggerDate = triggerDate;
	  this.triggerReason = triggerReason;
  }
}
