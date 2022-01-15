package com.its4u.buildfactory.model.placeholders;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@ToString
@Data

public class PlaceHolders implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	

	private PlaceHolderId placeHolderId;
	
	@JsonIgnore

	private Environments _environment;
	

	private String value;
		

	private String type;
	
	public PlaceHolders() {
		super();
	}
		
	public PlaceHolders(PlaceHolderId placeHolderId, Environments environment, String value,String type) {
		super();
		this.placeHolderId = placeHolderId;
		this._environment = environment;
		this.value = value;
		this.type = type;
	}

	@Override
	public String toString() {
		return "PlaceHolders [placeHolderId=" + placeHolderId + "]";
	}
	

}
