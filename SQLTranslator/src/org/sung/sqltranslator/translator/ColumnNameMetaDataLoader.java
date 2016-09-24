package org.sung.sqltranslator.translator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.MetaColumn;
import org.sung.sqltranslator.translator.vo.MetaData;
import org.sung.sqltranslator.translator.vo.MetaTable;



public class ColumnNameMetaDataLoader {
	private static Log log = LogFactory.getLog(ColumnNameMetaDataLoader.class);
	
	private static final String FILE_NAME = "D:\\my_projects\\SQLTranslator Ver 1.0\\SQLTranslator\\test\\maps\\meta_data.txt";

	private static ColumnNameMetaDataLoader instance = new ColumnNameMetaDataLoader();

	private MetaData metaData;

	public static ColumnNameMetaDataLoader getInstance() {
		return instance;
	}

	public MetaData getMetaData() {
		if (metaData == null)
			load();
		return metaData;
	}

	private void load() {
		File f = new File(FILE_NAME);
		FileReader fr = null;
		LineNumberReader lr = null;
		try {

			fr = new FileReader(f);
			lr = new LineNumberReader(fr);

			String line = null;
			String tName = null;
			String[] row = null;
			MetaTable table = null;
			List<MetaTable> tables = new ArrayList<MetaTable>();
			MetaColumn column = null;

			while ((line = lr.readLine()) != null) {

				row = line.split(",");
				if (row[0] == null || "".equals(row[0].trim()))
					continue;

				if (tName == null || !tName.equals(row[0])) {
					tName = row[0];
					table = new MetaTable();
					table.setName(tName);
					table.setTobeName(row[2]);
					tables.add(table);
				}
				column = new MetaColumn();
				column.setName(row[1]);
				column.setTobeName(row[3]);
				column.setDesc(row[4]);
				table.addColumn(column);

			}

			// if (log.isDebugEnabled()) {
			// for (MetaTable t : tables)
			// log.debug(t.toString());
			// }

			metaData = new MetaData();
			metaData.addAllMetaTable(tables);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				lr.close();
			} catch (Exception e) {
			}

		}

	}

}