import java.io.File;
import java.io.FileFilter;

public class XMLFileFilter implements FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return false;
		} else {
			String name = file.getName();
			if (name.endsWith(".xml"))
				return true;
			else
				return false;
		}
	}
}
