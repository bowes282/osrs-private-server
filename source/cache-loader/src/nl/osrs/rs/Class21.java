package nl.osrs.rs;

import java.lang.reflect.Field;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 


final class Class21
{

	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append( this.getClass().getName() );
		result.append( " Object {" );
		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		//print field names paired with their values
		for ( Field field : fields  ) {
			result.append("  ");
			try {
				result.append( field.getName() );
				result.append(": ");
				//requires access to private field:
				result.append( field.get(this) );
			} catch ( IllegalAccessException ex ) {
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	public Class21()
	{
	}
	
	public byte aByteArray368[];
	public int anInt369;
	public int anInt370;
	public int anInt371;
	public int anInt372;
	public int anInt373;
	public int anInt374;
	public int anInt375;
	public int anInt376;
	public int anInt377;
	public int anInt378;
	public int anInt379;
	public int anInt380;
	public int anInt381;
	public int anInt382;
	public int anInt383;
	public int anInt384;
}
