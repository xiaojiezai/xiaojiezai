import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Main {
	
	public static void main(String[] args) {
		File dir = new File("D:\\demo_renren");
		File results = new File("D:\\xiaojiezai");
		File[] xmlFiles = dir.listFiles(new XMLFileFilter()); //所有XML文件.
		int count = 1;
		for (File xmlFile : xmlFiles) {
			SAXReader reader = new SAXReader();
			Document document = null;
			try {
				document = reader.read(xmlFile);
			} catch (DocumentException e1) {
				System.out.println("xml error : " + xmlFile);
				continue; // xml文件解析错误，跳过解析下一个.
			}
			Element item = document.getRootElement().element("相片").element("item");
			String galleryName = item.element("相册名").getData().toString().replaceAll("/", "_").replaceAll("\\.", "");
			File pic = null;
			try {
				pic = download(item.element("照片").getData().toString());
			} catch (IOException e) {
				System.out.println("error download : " + item.element("照片").getData().toString());
				continue;
			}
			File result = new File(results, galleryName);
			if (!result.exists()) {
				result.mkdirs();
			}
			if ((new File(result, pic.getName())).exists()) {
				count++;
				System.out.println("文件已存在: " + pic + " to dir: " + result);
				continue; //该文件已抓取
			}
			try {
				FileUtils.copyFileToDirectory(pic, result);
			} catch (IOException e) {
				System.out.println("error copy file : " + pic + " to dir: " + result);
				continue;
			}
			FileUtils.deleteQuietly(pic);
			System.out.println("success catch pic :" + count ++ + "/" + xmlFiles.length + " from:" + galleryName);
		}
	}
	/**
	 * 下载网络图片到本地.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static File download(String url) throws IOException {
		File file = new File(FileUtils.getTempDirectory(), getFileName(url));
		URL uri = new URL(url);
		DataInputStream dis = new DataInputStream(uri.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = dis.read(buffer)) > 0) {
			fos.write(buffer, 0, length);
		}
		dis.close();
		fos.close();
		return file;
	}
	
	private static String getFileName(String url) {
		return url.split("/")[url.split("/").length - 1];
	}
}