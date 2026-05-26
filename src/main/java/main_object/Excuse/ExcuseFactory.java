/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main_object.Excuse;

/**
 *
 * @author Ирина
 */
public class ExcuseFactory {
    public static ExcuseParams createFromForm(String eventDescription,
                                               String desiredAction, String recipient,
                                               String formalityLevel, String urgency,
                                               String tone, boolean selfIronyAllowed,
                                               String customDetails, String length) {
        ExcuseParams params = new ExcuseParams();
        params.setEventDescription(eventDescription);
        params.setDesiredAction(desiredAction);
        params.setLength(Length.fromCode(length));
        params.setRecipient(recipient);
        params.setFormalityLevel(FormalityLevel.fromCode(formalityLevel));
        params.setUrgency(Urgency.fromCode(urgency));
        params.setTone(Tone.fromCode(tone));
        params.setSelfIronyAllowed(selfIronyAllowed);
        params.setCustomDetails(customDetails);
        return params;
    }
    
     public static ExcuseParams createFromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        ExcuseParams params = new ExcuseParams();
        params.setId(rs.getLong("id"));
        params.setEventDescription(rs.getString("event_description"));
        params.setDesiredAction(rs.getString("desired_action"));
        params.setLength(Length.fromCode(rs.getString("length")));
        params.setRecipient(rs.getString("recipient"));
        params.setFormalityLevel(FormalityLevel.fromCode(rs.getString("formality_level")));
        params.setUrgency(Urgency.fromCode(rs.getString("urgency")));
        params.setTone(Tone.fromCode(rs.getString("tone")));
        params.setSelfIronyAllowed(rs.getBoolean("self_irony_allowed"));
        params.setCustomDetails(rs.getString("custom_details"));
        return params;
    }
}
