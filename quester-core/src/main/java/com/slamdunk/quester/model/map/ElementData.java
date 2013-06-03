package com.slamdunk.quester.model.map;

public class ElementData {
	public MapElements element;
	
	public ElementData() {
	}
	
	public ElementData(MapElements element) {
		this.element = element;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementData)) {
			return false;
		}
		return ((ElementData)obj).element == element;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal();
	}
}
