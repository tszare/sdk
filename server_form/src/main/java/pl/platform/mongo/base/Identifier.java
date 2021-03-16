package pl.platform.mongo.base;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class Identifier<A extends Serializable> extends BaseModel implements Serializable {
	private static final long serialVersionUID = -3379530681605262223L;
	public static final String ID = "id";
	public static final String CREATE_TIME = "createTime";
	/**
	 * 以@Id注解的字段，将被设置为mongodb的_id字段
	 */
	@Id
	protected A id;

	public Identifier() {
	}

	public Identifier(A id) {
		super();
		this.id = id;
	}

	public A getId() {
		return id;
	}

	public void setId(A id) {
		this.id = id;
	}

}
