package pl.platform.mongo.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

public class BaseModel implements Serializable {
	private static final long serialVersionUID = -4397530982811276754L;
	protected static Log log = LogFactory.getLog(BaseModel.class);

//	@Override
//	public String toString() {
//		try {
//			return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
//		} catch (Exception e) {
//			log.error("fatalError", e);
//			return super.toString();
//		}
//	}
}
