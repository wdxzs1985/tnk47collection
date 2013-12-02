package robot.tnk47.gift;

import net.sf.json.JSONObject;
import robot.AbstractEventHandler;
import robot.Robot;

public class GiftHandler extends AbstractEventHandler {

	public GiftHandler(final Robot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		String html = this.httpGet("/gift");

		JSONObject jsonPageParams = this.resolvePageParams(html);
		if (jsonPageParams != null) {
			JSONObject firstPageData = jsonPageParams
					.getJSONObject("firstPageData");
			// JSONArray giftDtos = firstPageData.getJSONArray("giftDtos");
			if (this.log.isInfoEnabled()) {
				int totalCount = firstPageData.getInt("totalCount");
				int totalPage = firstPageData.getInt("totalPage");
				this.log.info(String.format("totalCount: %d,totalPage: %d",
						totalCount, totalPage));
			}
		}

		return ("/mypage");
	}

}
