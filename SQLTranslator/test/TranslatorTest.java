import org.sung.sqltranslator.translator.ColumnNameTranslator;


public class TranslatorTest {

	@org.junit.Test
	public void testColumnNameTranslator() {
		ColumnNameTranslator ma = new ColumnNameTranslator();

		ma.analize("D:\\my_projects\\SQLTranslator Ver 1.0\\SQLTranslator\\test\\maps\\sqlMapTest.xml");
		// ma.analize("D:\\Temp\\sqls");
		// ma.analize("D:\\Programs\\SQLTranslator\\test\\maps\\sqlMapComplexSelect.xml");

		System.out.print(ma.getResult());
	}

}