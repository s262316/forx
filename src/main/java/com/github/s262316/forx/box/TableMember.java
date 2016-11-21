package com.github.s262316.forx.box;

public interface TableMember
{
	public TableBox table();

	public void computeProperties();
	public void set_container(TableMember tb);
	public void set_table(TableBox tb);
}
