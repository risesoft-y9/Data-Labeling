package net.risesoft.y9public.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "message", namespace = "risesoft.model")
@NoArgsConstructor
@Data
public class Message implements Serializable {

	private static final long serialVersionUID = 2247331928742649549L;

	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAIL = 50000;

	private int code;
	private String msg;
	private Object data;
}