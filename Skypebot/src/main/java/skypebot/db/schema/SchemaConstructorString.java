package skypebot.db.schema;

public class SchemaConstructorString {
	
	private SchemaConstructorType type;
	private String string;

	public SchemaConstructorString(String s, SchemaConstructorType type) {
		string = s;
		this.type = type;

	}
	
	public String getString(){
		return string;
	}
	
	public SchemaConstructorType getType(){
		return type;
	
	}
	
	

}
