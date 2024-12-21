package com.cosmo.cats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CosmoCat {
    private String id;
    private String name;
    private String spacesuit;
    private String planet;
}
