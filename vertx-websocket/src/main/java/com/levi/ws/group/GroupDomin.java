package com.levi.ws.group;

import lombok.Data;

import java.util.Set;

@Data
public class GroupDomin {
    private String id;
    private String name;
    private Set<String> members;
}
