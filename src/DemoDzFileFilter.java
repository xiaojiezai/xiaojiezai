import java.io.File;
import java.io.FileFilter;


public class DemoDzFileFilter implements FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return false;
		} else {
			String name = file.getName();
			if (name.startsWith("demo_DZ_loca"))
				return true;
			else
				return false;
		}
	}
}
