package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tag {

    private Long id;
    private String vocabulary;
    private String tag;
    private String displayKeyword;
}
