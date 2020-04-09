package com.fajar.shopkeeping.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.service.AccountService;
import com.fajar.shopkeeping.service.EntityService;
import com.fajar.shopkeeping.service.ReportService;
import com.fajar.shopkeeping.service.TransactionService;

public class MainHandler {

	protected BasePage page;
	protected static final AppHandler APP_HANDLER = AppHandler.getInstance();
	protected final AccountService accountService = AccountService.getInstance();
	protected final ReportService reportService = ReportService.getInstance();
	protected final EntityService entityService = EntityService.getInstance();
	protected final TransactionService transactionService = TransactionService.getInstance();

	public MainHandler() {
		init();
	}
	
	public MainHandler(BasePage page) {
		this.page = page;
		init();
	}

	protected void init() {  }

	public ActionListener navigationListener(final int pageCode) {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				APP_HANDLER.navigate(pageCode);
			}
		};
	}

	public void dismissPage() {
		if (null == page) {
			System.out.println("page is null");
			return;
		}
		page.dismiss();
	}

	public void start() {
		page.show();
	}

	/**
	 * set this class as the handler of the page
	 */
	public void setPageHandler() {

		if (page == null) {

			System.out.println(this.getClass().getCanonicalName() + "Page is null");
			return;
		}

		page.setAppHandler(this);
		page.onShow();
	}

}
