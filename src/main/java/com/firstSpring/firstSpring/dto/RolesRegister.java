package com.firstSpring.firstSpring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * @author Daniel Mora Cantillo
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolesRegister {
    private List<String> roleListName;
}
