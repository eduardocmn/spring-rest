package br.com.restSpring.dto;

import java.io.Serializable;

public class IP implements Serializable{
	
	private static final long serialVersionUID = 4915132258791738729L;

	private String status;
	
	private Data data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
}
