/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Ирина
 */
public class ExcuseParams {
    private long id;
    private EventType eventType;              
    private String recipient;                 
    private FormalityLevel formalityLevel;    
    private Urgency urgency;                  
    private Tone tone;                       
    private boolean selfIronyAllowed;         
    
    private String customDetails;             
    private String preferredLength;          
    private boolean includeSuggestions;       
    private Map<String, String> metadata;     
    
    private LocalDateTime lastModified;
    
    public ExcuseParams() {}
    
    public boolean isValid() {
      return eventType != null && 
             recipient != null && !recipient.trim().isEmpty() &&
             formalityLevel != null &&
             urgency != null &&
             tone != null;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
        updateLastModified();
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        updateLastModified();
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
        updateLastModified();
    }
    
    public FormalityLevel getFormalityLevel() {
        return formalityLevel;
    }
    
    public void setFormalityLevel(FormalityLevel formalityLevel) {
        this.formalityLevel = formalityLevel;
        updateLastModified();
    }
    
    public Urgency getUrgency() {
        return urgency;
    }
    
    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
        updateLastModified();
    }
    
    public Tone getTone() {
        return tone;
    }
    
    public void setTone(Tone tone) {
        this.tone = tone;
        updateLastModified();
    }
    
    public boolean isSelfIronyAllowed() {
        return selfIronyAllowed;
    }
    
    public void setSelfIronyAllowed(boolean selfIronyAllowed) {
        this.selfIronyAllowed = selfIronyAllowed;
        updateLastModified();
    }
    
    public String getCustomDetails() {
        return customDetails;
    }
    
    public void setCustomDetails(String customDetails) {
        this.customDetails = customDetails;
        updateLastModified();
    }
    
    public String getPreferredLength() {
        return preferredLength;
    }
    
    public void setPreferredLength(String preferredLength) {
        this.preferredLength = preferredLength;
        updateLastModified();
    }
    
    public boolean isIncludeSuggestions() {
        return includeSuggestions;
    }
    
    public void setIncludeSuggestions(boolean includeSuggestions) {
        this.includeSuggestions = includeSuggestions;
        updateLastModified();
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        updateLastModified();
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventType, recipient, formalityLevel, urgency, tone, selfIronyAllowed);
    }
    
    public void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
}
