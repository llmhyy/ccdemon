package ccdemon.model;

public class OccurrenceTable {
	/**
	 * Each column of {@code table} represents a configuration point, each
	 * row of {@code table} represents a clone instance, and each entry of
	 * {@code table} means the value of a configuration point appears in a
	 * clone instance.
	 * 
	 * Note that the order of configuration point should be the same the 
	 * configuration points extracted in copy-paste action.
	 */
	private String[][] table;

	/**
	 * @param table
	 */
	public OccurrenceTable(String[][] table) {
		super();
		this.table = table;
	}

	/**
	 * @return the table
	 */
	public String[][] getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String[][] table) {
		this.table = table;
	}
	
	@Override
	public String toString(){
		int fixedLength = 30;
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<table.length; i++){
			for(int j=0; j<table[0].length; j++){
				buffer.append(table[i][j]);
				for(int k=0; k<fixedLength-table[i][j].length(); k++){
					buffer.append(" ");
				}
			}
			
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
