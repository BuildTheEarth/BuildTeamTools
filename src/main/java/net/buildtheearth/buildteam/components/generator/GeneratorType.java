package net.buildtheearth.buildteam.components.generator;

public enum GeneratorType {

    HOUSE("House"),
    ROAD("Road"),
    RAILWAY("Railway"),
    TREE("Tree");

    private String name;

    GeneratorType(String name){
        this.name = name;
    }

}
