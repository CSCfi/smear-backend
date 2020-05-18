package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Station {

    private int id;
    private String name;
}
