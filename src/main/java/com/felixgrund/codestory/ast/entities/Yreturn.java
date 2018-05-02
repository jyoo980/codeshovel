package com.felixgrund.codestory.ast.entities;

public class Yreturn {

	public static final String TYPE_NONE = "";

	private String type;

	public Yreturn(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof Yreturn) {
			Yreturn otherReturn = (Yreturn) obj;
			ret = this.type.equals(otherReturn.getType());
		}
		return ret;
	}

	public String getType() {
		return type;
	}
}