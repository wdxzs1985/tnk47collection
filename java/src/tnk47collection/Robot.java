package tnk47collection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import tnk47collection.common.CommonHttpClient;

public class Robot implements Runnable {

	public static void main(String[] args) {
		Thread thread = new Thread(new Robot());
		try {
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static final String HOST = "http://tnk47.ameba.jp";

	private final CommonHttpClient httpClient;
	private final File cookieFile;
	private final Map<String, EventHandler> handlerMapping;

	public Robot() {
		this.httpClient = new CommonHttpClient();
		this.cookieFile = new File("cookie");
		this.httpClient.loadCookie(this.cookieFile);
		this.handlerMapping = new HashMap<String, EventHandler>();
		this.handlerMapping.put("/", new HomeHandler(this));
		this.handlerMapping.put("/login", new LoginHandler(this));
		this.handlerMapping.put("/mypage", new MypageHandler(this));
		this.handlerMapping.put("/event/marathon", new MarathonHandler(this));
		this.handlerMapping.put("/event/marathon/event-quest",
				new MarathonQuestHandler(this));
		this.handlerMapping.put("/quest", new HomeHandler(this));

		this.dispatch("/");
	}

	private EventHandler nextHandler = null;

	public void dispatch(String event) {
		this.dispatch(event, null);
	}

	public void dispatch(String event, Map<String, String> model) {
		this.nextHandler = this.handlerMapping.get(event);
		if (model != null) {
			for (Entry<String, String> entry : model.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				this.nextHandler.addAttribute(name, value);
			}
		}

	}

	@Override
	public void run() {
		while (this.nextHandler != null) {
			EventHandler currEventHandler = this.nextHandler;
			this.nextHandler = null;
			currEventHandler.handle();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.httpClient.saveCookie(this.cookieFile);
	}

	public CommonHttpClient getHttpClient() {
		return this.httpClient;
	}
}
