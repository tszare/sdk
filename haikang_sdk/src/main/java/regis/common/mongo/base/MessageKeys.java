package regis.common.mongo.base;

/**
 * Message keys, refers to the message.properties.
 */
public class MessageKeys {
	/** 500 for internal server error. */
	public static final int INTERNAL_SERVER_ERROR = 500;

	/** When the entity create failed, it will show this error message */
	public static final int ENTITY_CREATE_FAILED = 100001;

	/** When the entity update failed, it will show this error message */
	public static final int ENTITY_UPDATE_FAILED = 100002;

	/** When the entity action failed, it will show this error message */
	public static final int ENTITY_ACTION_NOT_ALLOW = 100003;

	/** When get entity by id failed, it will show this error message */
	public static final int ENTITY_NOT_FOUND_FOR_IDENTIFIER = 100004;

	/** When request entity not valid for id, it will show this error message */
	public static final int ENTITY_NOT_VALID_FOR_IDENTIFIER = 100005;

	/** When call wechat API get error, it will show this error message */
	public static final int WECHAT_API_ERROR = 100006;

	/** When call weibo API get error, it will show this error message */
	public static final int WEIBO_API_ERROR = 100007;

	/** When request params validation failed, it will show this error message */
	public static final int REQUEST_PARAMS_INVALID = 100008;

	/** When unique check failed, it will show this error message. */
	public static final int UNIQUE_VALID_FAILED = 100009;

	/** No user found for user query condition. **/
	public static final int NO_USERS_FOUND_FOR_CONDITIONS = 100010;

	/** When no account found for channel and appid, it will show this error message */
	public static final int ACCOUNT_NOT_FOUND_FOR_CHANNEL_APPID = 100011;

	/** When no invalid account found for channel and appid, it will show this error message */
	public static final int ACCOUNT_NOT_VALID_FOR_CHANNEL_APPID = 100012;

	/** When call alipay API get error, it will show this error message */
	public static final int ALIPAY_API_ERROR = 100013;

	/** When third part API resources get error, it will show this error message */
	public static final int THIRDPART_ERROR = 100014;

	/** When get entity by condition failed, it will show this error message */
	public static final int ENTITY_NOT_FOUND_FOR_CONDITION = 100015;

	/** When call wechat trade API get error, it will show this error message */
	public static final int WECHAT_TRADE_API_ERROR = 100016;

	/** When call channel API send customer service message error, it will show this error message */
	public static final int CUSTOMER_SERVICE_API_ERROR = 100017;

	/** When call wechat CP API get error, it will show this error message */
	public static final int WECHAT_CP_API_ERROR = 100018;

	/** If required field not found, it will show this error message. */
	public static final int REQUIRED_FIELD_NOT_FOUND = 200001;

	/** If field value illegal, it will show this error message. */
	public static final int ILLEGAL_VALUE_FOR_FIELD = 200002;

	/** Required field not found for specific type message. */
	public static final int REQUIRED_FIELD_NOT_FOUND_FOR_MESSAGE = 200003;

	/** Not supported message type. */
	public static final int MESSAGE_TYPE_NOT_SUPPORTED = 200004;

	/** If the startTime is earlier than weixin's earliest time, it will show this error message */
	public static final int STATISTICS_BEGIN_TIME_INVALID = 200005;

	/** When user interaction time with account is beyond 48-hours limit, it will show this error message. */
	public static final int USER_INTERACTION_TIME_BEYOND_LIMIT = 200006;

	/** When call API with invalid request parameters or post body, it will show this error message */
	public static final int INVALID_REQUEST_PARAMETERS_OR_POST_BODY = 200007;

	/** Not supported media material type. */
	public static final int MEDIA_MATERIAL_TYPE_NOT_SUPPORTED = 200008;

	/** When the material file size exceeds the limit accordingly, it will show this error message. */
	public static final int MEDIA_MATERIAL_FILE_SIZE_BEYOND_LIMIT = 200009;

	/** When delete the material for the binding message, it will show this error message. */
	public static final int MEDIA_MATERIAL_NOT_DELETED = 200010;

	public static final String ENTITY_ACCOUNT = "Account";
	public static final String ENTITY_KEYWORD = "Keyword";
	public static final String ENTITY_MENU = "Menu";
	public static final String ENTITY_QRCODE = "QRCode";
	public static final String ENTITY_DEFAULT_RULE = "DefaultRule";
	public static final String ENTITY_INTERACT_MESSAGE = "InteractMessage";
	public static final String ENTITY_MASSIVE_MESSAGE = "MassiveMessage";
	public static final String ENTITY_MASS_SEND_USERS = "MassSendUsers";
	public static final String ENTITY_TEMPLATE_MESSAGE = "TemplateMessage";
	public static final String ENTITY_USER = "User";
	public static final String ENTITY_WEBHOOK_FORWARD_RULE = "WebHookForwardRule";
	public static final String ENTITY_MATERIAL = "Material";
	public static final String ENTITY_CUSTOMER_SERVICE_ACCOUNT = "CustomerServiceAccount";
	public static final String ENTITY_CUSTOMER_SERVICE_SESSION = "CustomerServiceSession";
	public static final String ENTITY_TRADE_CONFIG = "TradeConfig";
	public static final String ENTITY_ORDER = "Order";
	public static final String ENTITY_REFUND = "Refund";
	public static final String ENTITY_REDPACK = "Redpack";
	public static final String ENTITY_WECHAT_CP_SUITE = "WechatCPSuite";
	public static final String ENTITY_WECHAT_CP_CORP = "WechatCPCorp";
	public static final String ENTITY_WECHAT_CP_USER = "User";
}
