package tnk47collection;

import tnk47collection.work.DownloadHTML;
import tnk47collection.work.MakeCSV;
import tnk47collection.work.MakeJSON;
import tnk47collection.work.MakeRawText;

public class Application {

	private static int[] switcher = { 0, 1, 1, 1, 0, 0, 0 };

	public static void main(String[] args) {
		if (switcher[0] == 1) {
			RunDownloadHTML();
		}
		if (switcher[1] == 1) {
			RunMakeRawText();
		}
		if (switcher[2] == 1) {
			RunMakeCsv();
		}
		if (switcher[3] == 1) {
			RunMakeJson();
		}
	}

	public static void RunDownloadHTML() {
		for (int i = 1; i <= 7; i++) {
			Thread thread = new Thread(new DownloadHTML(i));
			try {
				thread.start();
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void RunMakeRawText() {
		for (int i = 1; i <= 7; i++) {
			Thread thread = new Thread(new MakeRawText(i));
			try {
				thread.start();
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void RunMakeCsv() {
		for (int i = 1; i <= 7; i++) {
			Thread thread = new Thread(new MakeCSV(i));
			try {
				thread.start();
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void RunMakeJson() {
		Thread thread = new Thread(new MakeJSON());
		try {
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
