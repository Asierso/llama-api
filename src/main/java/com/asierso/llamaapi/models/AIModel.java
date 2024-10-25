package com.asierso.llamaapi.models;

public class AIModel {
	private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@Override
	public String toString() {
		return "Model [name=" + name + "]";
	}
}
