package es.amplia.oda.operation.update.dataStructs;

public class Array {
	String name;
	String version;
	String type;
	String downloadUrl;
	String path;
	int order;
	String operation;
	String[] validators;
	int size;
	String oldVersion;
	String oldName;
	String oldPath;

	public Array() {
	}

	public Array(String name, String version, String type, String downloadUrl, String path, int order, String operation, String[] validators, int size, String oldVersion, String oldName, String oldPath) {
		this.name = name;
		this.version = version;
		this.type = type;
		this.downloadUrl = downloadUrl;
		this.path = path;
		this.order = order;
		this.operation = operation;
		this.validators = validators;
		this.size = size;
		this.oldVersion = oldVersion;
		this.oldName = oldName;
		this.oldPath = oldPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String[] getValidators() {
		return validators;
	}

	public void setValidators(String[] validators) {
		this.validators = validators;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getOldPath() {
		return oldPath;
	}

	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}
}
