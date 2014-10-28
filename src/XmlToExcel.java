import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlToExcel {
	private static EasyMap<String, String> map = new EasyMap<String, String>().easyPut("demo_DZ_loca13", "朝阳区").easyPut("demo_DZ_loca23", "海淀区").easyPut("demo_DZ_loca33", "东城区").easyPut("demo_DZ_loca43", "西城区").easyPut("demo_DZ_loca53", "丰台区").easyPut("demo_DZ_loca63", "昌平区")
			.easyPut("demo_DZ_loca73", "其他地区").easyPut("demo_DZ_loca83", "大兴区").easyPut("demo_DZ_loca93", "通州区").easyPut("demo_DZ_loca103", "石景山区").easyPut("demo_DZ_loca113", "顺义区").easyPut("demo_DZ_loca123", "房山区");

	private static EasyMap<String, List<String>> cache = new EasyMap<String, List<String>>();
	
	public static void main(String[] args) {
		File root = new File("D:\\xiaojiezai\\xmls");
		File[] dirs = root.listFiles();

		for (File dir : dirs) {
			if (dir.isFile()) {
				continue;// 列出所有文件夹,排除掉文件.
			}
			System.out.println("start process : " + dir.getName());
			File excels = new File("D:\\xiaojiezai\\excels");
			if (!excels.exists()) {
				excels.mkdirs();
			}
			
			File excel = new File(excels, dir.getName() + ".xls"); // 生成文件夹名对应的excel.
			if (excel.exists()) {
				excel.delete(); //如果文件存在，则删除重建.
			}
			WritableWorkbook book = null;
			WritableSheet sheet = null;
			try {
				book = Workbook.createWorkbook(excel); // 可写的excel.
				sheet = book.createSheet("sheet 1", 0);
				setTitle(sheet);//设置宽度，和标题.
			} catch (Exception e) {
				System.out.println("create excel error : " + excel);
				continue; // 创建excel错误.
			}

			File[] xmls = dir.listFiles(new XMLFileFilter()); // 这个文件夹下所有的xml文件.
			int rowNum = 1;
			for (File xml : xmls) {
				SAXReader reader = new SAXReader();
				Document document = null;
				try {
					document = reader.read(xml);
				} catch (DocumentException e1) {
					System.out.println("xml error : " + xml);
					continue; // xml文件解析错误，跳过解析下一个.
				}
				try {
					Element shop = document.getRootElement().element("list").element("item").element("shop");
					@SuppressWarnings("unchecked")
					List<Element> items = shop.elements();
					for (Element item : items) {
						String name = item.element("name").getData().toString();
						String theme = map.containsKey(excel.getName().split("\\.")[0]) ? map.get(excel.getName().split("\\.")[0]) : "";
						if (exists(name, theme)) {
							continue; //这个shop.name在这个shop.theme里已经存在了，则不写入excel.
						}
						String price = item.element("price").getData().toString();
						String score = item.element("score").getData().toString();
						Label cellTheme = new Label(0, rowNum, theme);
						sheet.addCell(cellTheme);
						Label cellName = new Label(1, rowNum, name);
						sheet.addCell(cellName);
						Label cellPrice = new Label(2, rowNum, price);
						sheet.addCell(cellPrice);
						Label cellScore = new Label(3, rowNum, score);
						sheet.addCell(cellScore);
						rowNum++;
					}
				} catch (Exception e) {
					System.out.println("save excel error : " + excel);
					continue;
				}
			}
			try {
				book.write();
				book.close();
			} catch (Exception e) {
				System.out.println("save excel error : " + excel);
				continue;
			}
		}
		System.out.println("end process !");
	}
	
	private static boolean exists(String name, String theme) {
		if (!cache.containsKey(theme)) {
			List<String> nameList = new ArrayList<String>();
			nameList.add(name);
			cache.easyPut(theme, nameList);
			return false;
		} else {
			List<String> nameListAlreayInTheme = cache.get(theme);
			if (nameListAlreayInTheme.contains(name)) {
				return true;
			} else {
				nameListAlreayInTheme.add(name);
				return false;
			}
		}
	}
	
	private static void setTitle(final WritableSheet sheet) throws RowsExceededException, WriteException {
		Label themeTitle = new Label(0, 0, "theme");
		sheet.addCell(themeTitle);
		sheet.setColumnView(0, 300);
		Label nameTitle = new Label(1, 0, "name");
		sheet.addCell(nameTitle);
		sheet.setColumnView(1, 300);
		Label priceTitle = new Label(2, 0, "price");
		sheet.addCell(priceTitle);
		sheet.setColumnView(2, 300);
		Label scoreTitle = new Label(3, 0, "score");
		sheet.addCell(scoreTitle);
		sheet.setColumnView(3, 300);
	}
}