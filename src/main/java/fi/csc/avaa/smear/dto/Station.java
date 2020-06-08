package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Station {

    private Long id;
    private String name;
    private String dcmiPoint;
}
