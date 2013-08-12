package vidada.util.table;


import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import archimedesJ.util.Lists;

/**
 * Base for models which use row based domain model data
 * @author pascal.buettiker
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class BaseTableModel<T> extends AbstractTableModel {

	protected final List<T> data;
	protected final List<BaseColumnProvider<T>> columns;
	
	public BaseTableModel(){
		this(new ArrayList<T>(), new ArrayList<BaseColumnProvider<T>>());
	}
	
	public BaseTableModel(List<T> items, Iterable<BaseColumnProvider<T>> cols){
		data =  items;
		columns = Lists.newList(cols);
	}
	
	public T getRowModel(int index) {
		return data.get(index);
	}
	
	
	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int c) {
		return columns.get(c).getColumnType();
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return columns.get(col).isEditable();
	}

	@Override
	public String getColumnName(int col) {
		return columns.get(col).getColumnName();
	}

	public void add(T item) {  
		data.add(item);
		this.fireTableDataChanged();  
	}  

	public void remove(T item) {  
		data.remove(item); 
		this.fireTableDataChanged();  
	}  
	
	
	@Override
	public Object getValueAt(int row, int col) {

		Object value = null;
		
		//TODO index bound checks
		value = columns.get(col).getValueOfRow(getRowModel(row));

		return value;
	}


	@Override
	public void setValueAt(Object value, int row, int col) {
		//TODO index bound checks
		columns.get(col).setValueOfRow(getRowModel(row), value);
		fireTableCellUpdated(row, col);
	}
	
}