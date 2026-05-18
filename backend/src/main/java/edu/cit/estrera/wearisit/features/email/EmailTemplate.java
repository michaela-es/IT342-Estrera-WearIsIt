package edu.cit.estrera.wearisit.features.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailTemplate {
    private String title;
    private String heading;
    private String greeting;
    private String bodyText;
    private String buttonText;
    private String buttonLink;
    private String footerNote;
}
