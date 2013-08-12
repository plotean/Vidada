package vidada.util.table;


/**
 * Base class for a column provider. Represents one Column <--> Model-Item binding
 * @author pascal.buettiker
 *
 */
public abstract class BaseColumnProvider<T> {
	
	private String columnName;
	private Class<?> columnType;
	private boolean editable;
	
	public BaseColumnProvider(String name, Class<?> dataType, boolean editable){
		setColumnName(name);
		setColumnType(dataType);
	}
	
	

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Class<?> getColumnType() {
		return columnType;
	}

	public void setColumnType(Class<?> columnType) {
		this.columnType = columnType;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public abstract Object getValueOfRow(T item);
	
	/**
	 * Can be overridden if the column should be editable
	 * @param row
	 * @param newValue
	 */
	public void setValueOfRow(T item, Object newValue) { }

}