package com.chris.gestionpersonal.models.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailDTO {
    private String[] toUser;
    private String subject;
    private String message;
}