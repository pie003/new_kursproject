/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Request;

import java.time.LocalDateTime;
import main_object.Excuse.ExcuseParams;

/**
 *
 * @author Ирина
 */
public class GenerationRequest {
    public static final int MAX_TEXT_LENGTH = 5000;
    public static final int MAX_PROMPT_LENGTH = 2000;
    
    private Long id;
    private Long userId;                    
    private ExcuseParams params;           
    private String generatedText;           
    private RequestStatus status;           
    private String errorMessage;            
    private LocalDateTime createdAt;        
    private LocalDateTime updatedAt;        
    private LocalDateTime completedAt;      
    private boolean isSaved;                
    private int retryCount;
    
    public GenerationRequest() {}
    
    public void markAsSuccess(String generatedText) {
        if (generatedText == null || generatedText.trim().isEmpty()) {
            throw new IllegalArgumentException("Generated text cannot be empty");
        }
        
        if (generatedText.length() > MAX_TEXT_LENGTH) {
            generatedText = generatedText.substring(0, MAX_TEXT_LENGTH);
        }
        
        this.generatedText = generatedText;
        this.status = RequestStatus.SAVED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.errorMessage = null;
    }
    
    public void saveResult() {
        if (this.status != RequestStatus.SAVED) {
            throw new IllegalStateException("Cannot save a request that is not successful");
        }
        this.isSaved = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public ExcuseParams getParams() {
        return params;
    }
    
    public void setParams(ExcuseParams params) {
        this.params = params;
    }
    
    public String getGeneratedText() {
        return generatedText;
    }
    
    public void setGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public boolean isSaved() {
        return isSaved;
    }
    
    public void setSaved(boolean saved) {
        isSaved = saved;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
